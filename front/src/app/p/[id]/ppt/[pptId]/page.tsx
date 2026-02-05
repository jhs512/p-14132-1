import { cookies } from "next/headers";
import Link from "next/link";

import Marp from "@marp-team/marp-core";

import ClientPage from "../ClientPage";

const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL;

// Mermaid 인코딩 함수 (서버사이드용)
function encodeMermaid(text: string): string {
  const json = JSON.stringify({ code: text, mermaid: { theme: "default" } });
  return Buffer.from(json).toString("base64");
}

// PlantUML 인코딩 함수
function encodePlantUML(text: string): string {
  const hex = Buffer.from(text).toString("hex");
  return "~h" + hex;
}

// 마크다운에서 mermaid/plantuml 코드블록을 이미지로 변환
function preprocessDiagrams(content: string): string {
  // ```mermaid 처리
  content = content.replace(/```mermaid\n([\s\S]*?)```/g, (_, code) => {
    const encoded = encodeMermaid(code.trim());
    return `![](https://mermaid.ink/svg/${encoded})`;
  });

  // ```uml 또는 ```plantuml 처리
  content = content.replace(
    /```(?:uml|plantuml)\n([\s\S]*?)```/g,
    (_, code) => {
      const encoded = encodePlantUML(code.trim());
      return `![](https://www.plantuml.com/plantuml/svg/${encoded})`;
    },
  );

  return content;
}

async function getPost(id: number) {
  const cookieStore = await cookies();
  const response = await fetch(`${API_BASE_URL}/post/api/v1/posts/${id}`, {
    headers: {
      cookie: cookieStore.toString(),
    },
    cache: "no-store",
  });

  if (!response.ok) {
    return null;
  }

  return response.json();
}

function extractPptContent(
  content: string,
  pptId: string,
): { title: string; marpContent: string } | null {
  const escapedId = pptId.replace(/[.*+?^${}()|[\]\\]/g, "\\$&");

  // SLOG 방식: <div markdown="1"> 안에 내용
  const patternWithDiv = new RegExp(
    `<details[^>]*\\bppt-id\\s*=\\s*["']${escapedId}["'][^>]*>[\\s\\S]*?<summary>\\s*(.*?)\\s*</summary>[\\s\\S]*?<div[^>]*markdown=["']1["'][^>]*>([\\s\\S]*?)</div>`,
    "i",
  );
  let match = content.match(patternWithDiv);

  // 기본 방식: details 내 직접 내용
  if (!match) {
    const patternDirect = new RegExp(
      `<details[^>]*\\bppt-id\\s*=\\s*["']${escapedId}["'][^>]*>[\\s\\S]*?<summary[^>]*>([^<]+)</summary>([\\s\\S]*?)</details>`,
    );
    match = content.match(patternDirect);
  }

  if (!match) return null;

  const title = match[1].trim();
  let marpContent = match[2].trim();

  // 끝에 있는 빈 슬라이드 구분자 제거 (---\n\n 또는 ---만 있는 경우)
  marpContent = marpContent.replace(/\n---\s*$/, "");

  // 다이어그램 중앙 정렬 스타일 (mermaid.ink, plantuml.com 이미지만)
  const diagramStyle = `
  section p:only-child {
    display: flex;
    justify-content: center;
    align-items: center;
    width: 100%;
    height: 100%;
    margin: 0;
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
  }
  section p:only-child img {
    max-height: 85%;
    max-width: 90%;
    object-fit: contain;
  }`;

  // Marp 헤더가 없으면 기본 헤더 추가
  if (!marpContent.trimStart().startsWith("---")) {
    marpContent = `---
marp: true
theme: default
paginate: true
style: |${diagramStyle}
---

${marpContent}`;
  } else {
    // 기존 헤더가 있으면 style 섹션에 다이어그램 스타일 추가
    const headerEndMatch = marpContent.match(/^---\n([\s\S]*?)\n---/);
    if (headerEndMatch) {
      let header = headerEndMatch[1];
      // 기존 style이 있으면 추가, 없으면 새로 생성
      if (header.includes("style:")) {
        header = header.replace(/(style:\s*\|)/, `$1${diagramStyle}`);
      } else {
        header += `\nstyle: |${diagramStyle}`;
      }
      marpContent = marpContent.replace(
        /^---\n[\s\S]*?\n---/,
        `---\n${header}\n---`,
      );
    }
  }

  return { title, marpContent };
}

export async function generateMetadata({
  params,
}: {
  params: Promise<{ id: string; pptId: string }>;
}) {
  const { id, pptId } = await params;
  const post = await getPost(parseInt(id));

  if (!post) {
    return { title: "Error - PPT" };
  }

  const pptData = extractPptContent(post.content, pptId);
  const title = pptData?.title || post.title;

  return {
    title: `Doc ${id} - ${title}`,
  };
}

export default async function Page({
  params,
}: {
  params: Promise<{ id: string; pptId: string }>;
}) {
  const { id: idStr, pptId } = await params;
  const id = parseInt(idStr);

  const post = await getPost(id);

  if (!post) {
    return (
      <div className="flex-1 flex items-center justify-center">
        <p className="text-red-500">글을 찾을 수 없습니다.</p>
      </div>
    );
  }

  const pptData = extractPptContent(post.content, pptId);

  if (!pptData) {
    return (
      <div className="flex-1 flex items-center justify-center text-red-500">
        PPT를 찾을 수 없습니다: {pptId}
        <br />
        <Link href={`/p/${id}`} className="text-blue-500 hover:underline">
          돌아가기
        </Link>
      </div>
    );
  }

  // mermaid/plantuml 코드블록을 이미지로 전처리
  const processedContent = preprocessDiagrams(pptData.marpContent);

  // Marp로 HTML 변환
  const marp = new Marp();
  const { html: rawHtml, css } = marp.render(processedContent);

  // Mermaid/PlantUML 다이어그램 중앙 정렬을 위해 HTML 후처리
  // 1. mermaid.ink/plantuml.com 이미지의 기존 스타일을 새 스타일로 교체
  let html = rawHtml.replace(
    /<img([^>]*src=["']https:\/\/(?:mermaid\.ink|www\.plantuml\.com)[^"']*["'])([^>]*)style="[^"]*"([^>]*)>/gi,
    '<img$1$2style="max-height:85%;max-width:90%;object-fit:contain;display:block;margin:auto;"$3>',
  );

  // 스타일이 없는 경우도 처리
  html = html.replace(
    /<img((?![^>]*style=)[^>]*src=["']https:\/\/(?:mermaid\.ink|www\.plantuml\.com)[^"']*["'][^>]*)>/gi,
    '<img$1 style="max-height:85%;max-width:90%;object-fit:contain;display:block;margin:auto;">',
  );

  // 2. 해당 이미지를 포함하는 p 태그에 스타일 적용 (position: absolute 제거, flex: 1로 공간 차지)
  html = html.replace(
    /<p>(\s*<img[^>]*src=["']https:\/\/(?:mermaid\.ink|www\.plantuml\.com)[^>]*>)\s*<\/p>/gi,
    '<p style="display:flex;justify-content:center;align-items:center;flex:1;width:100%;margin:0;">$1</p>',
  );

  return (
    <ClientPage postId={id} pptTitle={pptData.title} html={html} css={css} />
  );
}
