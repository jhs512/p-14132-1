export default function EmptyState() {
  return (
    <div className="flex flex-col items-center justify-center py-20 text-center">
      <span
        className="material-symbols-outlined text-toss-text-tertiary mb-3"
        aria-hidden="true"
        style={{ fontSize: 48 }}
      >
        search_off
      </span>
      <p className="text-toss-text-secondary text-[15px] font-medium">
        아직 등록된 프로젝트가 없어요
      </p>
      <p className="text-toss-text-tertiary text-[13px] mt-1">
        새로운 빌더의 프로젝트를 기다려주세요
      </p>
    </div>
  );
}
