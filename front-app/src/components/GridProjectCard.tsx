import { Link } from "react-router-dom";
import type { PostDto } from "../types/post";

const fmt = new Intl.NumberFormat("ko-KR", { notation: "compact" });

export default function GridProjectCard({ post }: { post: PostDto }) {
  return (
    <Link
      to={`/posts/${post.id}`}
      className="flex flex-col group"
    >
      <div className="relative w-full aspect-[4/3] rounded-lg overflow-hidden bg-toss-gray mb-2">
        {post.thumbnailImgUrl ? (
          <img
            src={post.thumbnailImgUrl}
            alt={post.title}
            className="w-full h-full object-cover transition-transform duration-500 group-hover:scale-105"
            loading="lazy"
          />
        ) : (
          <div className="w-full h-full bg-toss-gray" />
        )}
      </div>
      <div className="flex flex-col gap-1">
        <h3 className="text-toss-text-primary text-[15px] font-semibold leading-snug line-clamp-2">
          {post.title}
        </h3>
        <p className="text-toss-text-secondary text-[12px] truncate">
          {post.authorName}
        </p>
        <div className="flex items-center gap-3 mt-1 text-[12px] text-toss-text-tertiary">
          <span className="flex items-center gap-0.5">
            <span className="material-symbols-outlined" style={{ fontSize: 14 }}>
              visibility
            </span>
            {fmt.format(post.hitCount)}
          </span>
          <span className="flex items-center gap-0.5">
            <span className="material-symbols-outlined" style={{ fontSize: 14, fontVariationSettings: "'FILL' 1" }}>
              favorite
            </span>
            {fmt.format(post.likesCount)}
          </span>
        </div>
      </div>
    </Link>
  );
}
