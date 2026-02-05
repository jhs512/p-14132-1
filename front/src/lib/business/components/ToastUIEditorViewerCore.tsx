"use client";

import "@toast-ui/editor/dist/toastui-editor.css";
import "@toast-ui/editor/dist/theme/toastui-editor-dark.css";

// @ts-expect-error - 타입 정보 없음
import codeSyntaxHighlight from "@toast-ui/editor-plugin-code-syntax-highlight/dist/toastui-editor-plugin-code-syntax-highlight-all";
import tableMergedCell from "@toast-ui/editor-plugin-table-merged-cell";

import "@toast-ui/editor-plugin-table-merged-cell/dist/toastui-editor-plugin-table-merged-cell.css";

import { forwardRef, useMemo } from "react";

import { Viewer } from "@toast-ui/react-editor";

import { processMarkdownContent } from "../markdownUtils";

function youtubePlugin() {
  const toHTMLRenderers = {
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    youtube(node: any) {
      const html = renderYoutube(node.literal);
      return [
        { type: "openTag", tagName: "div", outerNewLine: true },
        { type: "html", content: html },
        { type: "closeTag", tagName: "div", outerNewLine: true },
      ];
    },
  };

  function renderYoutube(url: string) {
    url = url.replace("https://www.youtube.com/watch?v=", "");
    url = url.replace("http://www.youtube.com/watch?v=", "");
    url = url.replace("www.youtube.com/watch?v=", "");
    url = url.replace("youtube.com/watch?v=", "");
    url = url.replace("https://youtu.be/", "");
    url = url.replace("http://youtu.be/", "");
    url = url.replace("youtu.be/", "");

    let youtubeId = url;
    if (youtubeId.indexOf("?") !== -1) {
      const pos = url.indexOf("?");
      youtubeId = youtubeId.substring(0, pos);
    }

    return (
      '<div style="max-width:800px; margin-left:auto; margin-right:auto;" class="aspect-[16/9] relative my-4"><iframe class="absolute top-0 left-0 w-full h-full" src="https://www.youtube.com/embed/' +
      youtubeId +
      '" allow="accelerometer; autoplay; encrypted-media; gyroscope; picture-in-picture" allowfullscreen></iframe></div>'
    );
  }

  return { toHTMLRenderers };
}

function isExternalUrl(url: string): boolean {
  if (!url) return false;
  return url.startsWith("http://") || url.startsWith("https://");
}

// PlantUML 인코딩 함수
function encodePlantUML(text: string): string {
  // PlantUML uses a custom encoding: deflate -> base64 variant
  // 간단한 방식: hex encoding 사용 (~h prefix)
  const hex = Array.from(new TextEncoder().encode(text))
    .map((b) => b.toString(16).padStart(2, "0"))
    .join("");
  return "~h" + hex;
}

// Mermaid 인코딩 함수 (mermaid.ink 서버용)
function encodeMermaid(text: string): string {
  // mermaid.ink는 base64 인코딩 사용 (UTF-8 지원)
  const json = JSON.stringify({ code: text, mermaid: { theme: "default" } });
  // UTF-8 문자열을 바이트로 변환 후 base64 인코딩
  const bytes = new TextEncoder().encode(json);
  const binary = Array.from(bytes)
    .map((b) => String.fromCharCode(b))
    .join("");
  return btoa(binary);
}

export interface ToastUIEditorViewerCoreProps {
  initialValue: string;
  theme: "dark" | "light";
  postId?: string | number;
}

// eslint-disable-next-line @typescript-eslint/no-explicit-any
const ToastUIEditorViewerCore = forwardRef<any, ToastUIEditorViewerCoreProps>(
  (props, ref) => {
    // surl: 링크 처리
    const processedContent = useMemo(() => {
      if (props.postId) {
        return processMarkdownContent(props.initialValue, props.postId);
      }
      return props.initialValue;
    }, [props.initialValue, props.postId]);

    return (
      <Viewer
        theme={props.theme}
        plugins={[youtubePlugin, codeSyntaxHighlight, tableMergedCell]}
        ref={ref}
        initialValue={processedContent}
        customHTMLRenderer={{
          // eslint-disable-next-line @typescript-eslint/no-explicit-any
          heading(node: any, { entering, getChildrenText }: any) {
            return {
              type: entering ? "openTag" : "closeTag",
              tagName: `h${node.level}`,
              attributes: {
                id: getChildrenText(node).trim().replaceAll(" ", "-"),
              },
            };
          },
          // eslint-disable-next-line @typescript-eslint/no-explicit-any
          link(node: any, { entering }: any) {
            return {
              type: entering ? "openTag" : "closeTag",
              tagName: `a`,
              attributes: {
                href: node.destination,
                target: isExternalUrl(node.destination) ? "_blank" : "_self",
              },
            };
          },
          // eslint-disable-next-line @typescript-eslint/no-explicit-any
          codeBlock(node: any) {
            const language = node.info?.toLowerCase() || "";
            // ```uml 또는 ```plantuml 처리
            if (language === "uml" || language === "plantuml") {
              const encoded = encodePlantUML(node.literal);
              const imgUrl = `https://www.plantuml.com/plantuml/svg/${encoded}`;
              return [
                {
                  type: "openTag",
                  tagName: "div",
                  outerNewLine: true,
                  attributes: { class: "diagram-container" },
                },
                {
                  type: "html",
                  content: `<img src="${imgUrl}" alt="PlantUML Diagram" />`,
                },
                { type: "closeTag", tagName: "div", outerNewLine: true },
              ];
            }
            // ```mermaid 처리
            if (language === "mermaid") {
              const encoded = encodeMermaid(node.literal);
              const imgUrl = `https://mermaid.ink/svg/${encoded}`;
              return [
                {
                  type: "openTag",
                  tagName: "div",
                  outerNewLine: true,
                  attributes: { class: "diagram-container" },
                },
                {
                  type: "html",
                  content: `<img src="${imgUrl}" alt="Mermaid Diagram" />`,
                },
                { type: "closeTag", tagName: "div", outerNewLine: true },
              ];
            }
            // 기본 코드블록은 그대로 처리 (codeSyntaxHighlight 플러그인이 처리)
            return null;
          },
        }}
      />
    );
  },
);

ToastUIEditorViewerCore.displayName = "ToastUIEditorViewerCore";

export default ToastUIEditorViewerCore;
