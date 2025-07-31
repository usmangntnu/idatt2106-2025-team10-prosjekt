export type AuthRequest =
  | { email: string; password: string; username?: never }
  | { username: string; password: string; email?: never }
