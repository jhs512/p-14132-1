"use client";

import { useCallback, useEffect, useState } from "react";

import type { components } from "@/global/backend/apiV1/schema";
import client from "@/global/backend/client";
import { toast } from "sonner";

import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
  AlertDialogTrigger,
} from "@/components/ui/alert-dialog";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";

import {
  Download,
  Eye,
  FileImage,
  Paperclip,
  Trash2,
  Upload,
} from "lucide-react";

type PostGenFileDto = components["schemas"]["PostGenFileDto"];

interface PostGenFileManagerProps {
  postId: number;
}

function getFileSizeHr(size: number): string {
  return size >= 1024 * 1024
    ? `${(size / (1024 * 1024)).toFixed(1)}MB`
    : size >= 1024
      ? `${(size / 1024).toFixed(1)}KB`
      : `${size}B`;
}

export default function PostGenFileManager({
  postId,
}: PostGenFileManagerProps) {
  const [files, setFiles] = useState<PostGenFileDto[]>([]);
  const [uploading, setUploading] = useState(false);
  const [previewFile, setPreviewFile] = useState<PostGenFileDto | null>(null);

  const loadFiles = useCallback(() => {
    client
      .GET("/post/api/v1/posts/{postId}/genFiles", {
        params: { path: { postId } },
      })
      .then((res) => {
        if (res.data) {
          setFiles(res.data);
        }
      });
  }, [postId]);

  useEffect(() => {
    loadFiles();
  }, [loadFiles]);

  const handleUpload = async (files: FileList) => {
    setUploading(true);

    const formData = new FormData();
    for (const file of Array.from(files)) {
      formData.append("files", file);
    }

    try {
      const response = await fetch(
        `${process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8080"}/post/api/v1/posts/${postId}/genFiles/ATTACHMENT`,
        {
          method: "POST",
          body: formData,
          credentials: "include",
        },
      );

      const data = await response.json();

      if (response.ok) {
        toast.success(`${files.length}개의 파일이 업로드되었습니다.`);
        loadFiles();
      } else {
        toast.error(data.msg || "업로드 실패");
      }
    } catch {
      toast.error("업로드 중 오류가 발생했습니다.");
    } finally {
      setUploading(false);
    }
  };

  const handleDelete = async (fileNo: number) => {
    const res = await client.DELETE(
      "/post/api/v1/posts/{postId}/genFiles/{typeCode}/{fileNo}",
      {
        params: {
          path: { postId, typeCode: "ATTACHMENT", fileNo },
        },
      },
    );

    if (res.error) {
      toast.error(res.error.msg);
      return;
    }

    toast.success("파일이 삭제되었습니다.");
    loadFiles();
  };

  const attachments = files.filter((f) => f.typeCode === "ATTACHMENT");

  const getFullUrl = (path: string) =>
    `${process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8080"}${path}`;

  return (
    <>
      {/* 파일 미리보기 Dialog */}
      <Dialog open={!!previewFile} onOpenChange={() => setPreviewFile(null)}>
        <DialogContent className="max-w-3xl max-h-[90vh] overflow-y-auto">
          <DialogHeader>
            <DialogTitle>파일 미리보기</DialogTitle>
            <DialogDescription>
              {previewFile?.originalFileName}
            </DialogDescription>
          </DialogHeader>
          {previewFile && (
            <div className="flex flex-col items-center gap-4">
              {previewFile.fileExtTypeCode === "img" && (
                <img
                  src={getFullUrl(previewFile.publicUrl)}
                  alt={previewFile.originalFileName}
                  className="max-w-full max-h-[60vh] object-contain"
                />
              )}
              {previewFile.fileExtTypeCode === "audio" && (
                <audio
                  src={getFullUrl(previewFile.publicUrl)}
                  controls
                  className="w-full"
                />
              )}
              {previewFile.fileExtTypeCode === "video" && (
                <video
                  src={getFullUrl(previewFile.publicUrl)}
                  controls
                  className="max-w-full max-h-[60vh]"
                />
              )}
              {previewFile.fileExtTypeCode === "etc" && (
                <p className="text-muted-foreground">
                  이 파일 형식은 미리보기를 지원하지 않습니다.
                </p>
              )}
              <Button variant="outline" asChild>
                <a
                  href={getFullUrl(previewFile.downloadUrl)}
                  className="flex items-center gap-2"
                >
                  <Download className="w-4 h-4" />
                  {previewFile.originalFileName} (
                  {getFileSizeHr(previewFile.fileSize)}) 다운로드
                </a>
              </Button>
            </div>
          )}
          <DialogFooter>
            <Button variant="outline" onClick={() => setPreviewFile(null)}>
              닫기
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <FileImage className="w-5 h-5" />
            파일 관리
          </CardTitle>
        </CardHeader>
        <CardContent className="space-y-3">
          <div className="flex items-center gap-2">
            <Paperclip className="w-4 h-4" />
            <Label className="font-semibold">첨부파일</Label>
          </div>
          <div className="flex items-center gap-2">
            <Input
              type="file"
              multiple
              disabled={uploading}
              onChange={(e) => {
                const files = e.target.files;
                if (files && files.length > 0) {
                  handleUpload(files);
                  e.target.value = "";
                }
              }}
              className="max-w-xs"
            />
            {uploading && (
              <span className="text-sm text-muted-foreground flex items-center gap-1">
                <Upload className="w-4 h-4 animate-pulse" />
                업로드 중...
              </span>
            )}
          </div>
          {attachments.length === 0 && (
            <p className="text-muted-foreground text-sm">
              첨부파일이 없습니다.
            </p>
          )}
          <ul className="space-y-2">
            {attachments.map((file) => (
              <li
                key={file.id}
                className="flex items-center gap-2 text-sm bg-muted p-3 rounded-md"
              >
                <Paperclip className="w-4 h-4 text-muted-foreground" />
                <span className="flex-1 truncate">{file.originalFileName}</span>
                <Badge variant="secondary">
                  {(file.fileSize / 1024).toFixed(1)} KB
                </Badge>
                {["img", "audio", "video"].includes(file.fileExtTypeCode) && (
                  <Button
                    variant="ghost"
                    size="sm"
                    onClick={() => setPreviewFile(file)}
                  >
                    <Eye className="w-4 h-4" />
                  </Button>
                )}
                <Button variant="ghost" size="sm" asChild>
                  <a href={getFullUrl(file.downloadUrl)} target="_blank">
                    <Download className="w-4 h-4" />
                  </a>
                </Button>
                <AlertDialog>
                  <AlertDialogTrigger asChild>
                    <Button variant="ghost" size="sm">
                      <Trash2 className="w-4 h-4" />
                    </Button>
                  </AlertDialogTrigger>
                  <AlertDialogContent>
                    <AlertDialogHeader>
                      <AlertDialogTitle>파일 삭제</AlertDialogTitle>
                      <AlertDialogDescription>
                        &quot;{file.originalFileName}&quot; 파일을
                        삭제하시겠습니까?
                      </AlertDialogDescription>
                    </AlertDialogHeader>
                    <AlertDialogFooter>
                      <AlertDialogCancel>취소</AlertDialogCancel>
                      <AlertDialogAction
                        onClick={() => handleDelete(file.fileNo)}
                      >
                        삭제
                      </AlertDialogAction>
                    </AlertDialogFooter>
                  </AlertDialogContent>
                </AlertDialog>
              </li>
            ))}
          </ul>
        </CardContent>
      </Card>
    </>
  );
}
