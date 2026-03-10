const wsBase = import.meta.env.VITE_BACKEND_WS_URL || "ws://localhost:2800";

/**
 * Example:
 *   socketUrl(`/ws/room/${roomHash}/`)
 */
const socketUrl = (path) => {
  // Ensure no accidental double slashes
  if (!path.startsWith("/")) path = `/${path}`;
  return `${wsBase}${path}`;
};

export default socketUrl;
