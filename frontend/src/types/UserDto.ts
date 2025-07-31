export type CurrentUser =
  | { id: number; email: string; username?: never; roles: string[] }
  | {
      id: number
      username: string
      email?: never
      roles: string[]
    }
