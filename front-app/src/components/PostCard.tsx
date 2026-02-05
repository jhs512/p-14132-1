import { Link } from "react-router-dom";
import type { PostDto } from "../types/post";

const numberFormat = new Intl.NumberFormat("ko-KR", { notation: "compact" });

export default function PostCard({ post }: { post: PostDto }) {
  return (
    <Link
      to={`/posts/${post.id}`}
      className="flex flex-col bg-white rounded-2xl overflow-hidden shadow-[0_2px_8px_rgba(0,0,0,0.04)] transition-transform duration-300 hover:-translate-y-0.5 focus-visible:ring-2 focus-visible:ring-primary focus-visible:outline-none"
      style={{ contentVisibility: "auto", containIntrinsicSize: "0 280px" }}
    >
      {post.thumbnailImgUrl ? (
        <img
          src={post.thumbnailImgUrl}
          alt={post.title}
          className="w-full aspect-[4/3] object-cover bg-toss-gray"
          loading="lazy"
        />
      ) : (
        <div
          className="w-full aspect-[4/3] bg-toss-gray"
          role="img"
          aria-label={post.title}
        />
      )}
      <div className="p-4 flex flex-col gap-1.5 min-w-0">
        <h3 className="text-toss-text-primary text-[15px] font-semibold leading-snug line-clamp-2">
          {post.title}
        </h3>
        <p className="text-toss-text-secondary text-[13px] font-normal leading-normal truncate">
          {post.authorName}
        </p>
        <div className="mt-auto pt-2 flex items-center gap-1 text-toss-text-tertiary">
          <span
            className="material-symbols-outlined"
            aria-hidden="true"
            style={{ fontSize: 14 }}
          >
            visibility
          </span>
          <span
            className="text-[12px]"
            style={{ fontVariantNumeric: "tabular-nums" }}
          >
            {numberFormat.format(post.hitCount)}
          </span>
        </div>
      </div>
    </Link>
  );
}
