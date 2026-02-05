const skeletonCard = (
  <div className="flex flex-col bg-white rounded-2xl overflow-hidden shadow-[0_2px_8px_rgba(0,0,0,0.04)]">
    <div className="w-full aspect-[4/3] bg-toss-gray animate-pulse" />
    <div className="p-4 flex flex-col gap-2">
      <div className="h-4 w-3/4 bg-toss-gray rounded animate-pulse" />
      <div className="h-3 w-1/2 bg-toss-gray rounded animate-pulse" />
      <div className="h-3 w-1/4 bg-toss-gray rounded animate-pulse mt-2" />
    </div>
  </div>
);

export default function PostGridSkeleton() {
  return (
    <div className="grid grid-cols-2 gap-3">
      {Array.from({ length: 6 }, (_, i) => (
        <div key={i}>{skeletonCard}</div>
      ))}
    </div>
  );
}
