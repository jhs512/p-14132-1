import { Link } from "react-router-dom";
import type { PostDto } from "../types/post";

const fmt = new Intl.NumberFormat("ko-KR", { notation: "compact" });

export default function HorizontalProjectCard({ post }: { post: PostDto }) {
  return (
    <Link
      to={`/posts/${post.id}`}
      className="min-w-[240px] max-w-[240px] flex flex-col bg-white rounded-xl overflow-hidden shadow-[0_2px_10px_rgba(0,0,0,0.06)] border border-gray-100 shrink-0"
    >
      <div className="relative w-full aspect-[16/10] bg-toss-gray">
        {post.thumbnailImgUrl ? (
          <img
            src={post.thumbnailImgUrl}
            alt={post.title}
            className="w-full h-full object-cover"
            loading="lazy"
          />
        ) : null}
      </div>
      <div className="p-3.5 flex flex-col gap-1">
        <h3 className="text-toss-text-primary text-[15px] font-bold leading-snug truncate">
          {post.title}
        </h3>
        <p className="text-toss-text-secondary text-[13px] truncate">
          {post.authorName}
        </p>
        <div className="mt-1.5 flex items-center gap-3 text-[12px] text-toss-text-tertiary">
          <span className="flex items-center gap-0.5">
            <span className="material-symbols-outlined" style={{ fontSize: 14, fontVariationSettings: "'FILL' 1" }}>
              favorite
            </span>
            {fmt.format(post.likesCount)}
          </span>
          <span className="flex items-center gap-0.5">
            <span className="material-symbols-outlined" style={{ fontSize: 14 }}>
              visibility
            </span>
            {fmt.format(post.hitCount)}
          </span>
        </div>
      </div>
    </Link>
  );
}
