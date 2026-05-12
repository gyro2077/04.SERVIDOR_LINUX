const KEY = "monster_auth";

export const VALID_USER = "MONSTER";
export const VALID_PASS = "MONSTER9";

export function login(user: string, pass: string): boolean {
  if (user === VALID_USER && pass === VALID_PASS) {
    if (typeof window !== "undefined") {
      localStorage.setItem(KEY, "1");
    }
    return true;
  }
  return false;
}

export function isAuthed(): boolean {
  if (typeof window === "undefined") return false;
  return localStorage.getItem(KEY) === "1";
}

export function logout(): void {
  if (typeof window !== "undefined") {
    localStorage.removeItem(KEY);
  }
}