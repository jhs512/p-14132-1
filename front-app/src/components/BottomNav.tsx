import { useLocation, Link } from "react-router-dom";

const NAV_ITEMS = [
  { to: "/", icon: "home", label: "Home" },
  { to: "/category", icon: "grid_view", label: "Category" },
  { to: "/post", icon: "add_circle", label: "Post" },
  { to: "/activity", icon: "favorite", label: "Activity" },
  { to: "/my", icon: "person", label: "My" },
] as const;

export default function BottomNav() {
  const { pathname } = useLocation();

  return (
    <nav className="fixed bottom-0 left-1/2 -translate-x-1/2 z-50 w-full max-w-[480px] bg-white border-t border-gray-100">
      <div className="flex justify-around items-center h-[56px] px-2">
        {NAV_ITEMS.map(({ to, icon, label }) => {
          const active = pathname === to;
          return (
            <Link
              key={to}
              to={to}
              className={`flex flex-col items-center justify-center w-full h-full gap-0.5 transition-colors ${
                active ? "text-primary" : "text-toss-text-tertiary"
              }`}
            >
              <span
                className="material-symbols-outlined"
                style={{
                  fontSize: 24,
                  fontVariationSettings: active ? "'FILL' 1" : "'FILL' 0",
                }}
              >
                {icon}
              </span>
              <span className="text-[10px] font-medium">{label}</span>
            </Link>
          );
        })}
      </div>
      <div className="w-full bg-white" style={{ height: "env(safe-area-inset-bottom)" }} />
    </nav>
  );
}
