export const BACKEND_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

export type User = {
  id: string;
  username: string;
  email: string;
  firstName: string;
  lastName: string | null;
  phone: string | null;
  age: number | null;
  role: string;
  active: boolean;
  createdAt: string | null;
};

export type UserInput = {
  username: string;
  email: string;
  firstName: string;
  lastName: string;
  phone: string;
  age: number | null;
  password?: string;
};

export type AuthSession = { token: string; username: string; role: string; permissions: string[] };
export type UserPage = { users: User[]; currentPage: number; totalItems: number; totalPages: number; pageSize: number; hasNext: boolean };
type Envelope<T> = { success: boolean; message: string; data: T };
type ErrorEnvelope = { message?: string; error?: string };

async function call<T>(endpoint: string, method = 'GET', data?: object, token?: string): Promise<T> {
  const response = await fetch(BACKEND_URL + endpoint, {
    method,
    headers: {
      ...(data ? { 'Content-Type': 'application/json' } : {}),
      ...(token ? { Authorization: 'Bearer ' + token } : {}),
    },
    body: data ? JSON.stringify(data) : undefined,
  });
  const raw = await response.text();
  let parsed: unknown;
  try { parsed = raw ? JSON.parse(raw) : {}; } catch { parsed = { error: raw }; }
  if (!response.ok) {
    const detail = parsed as ErrorEnvelope;
    throw new Error(detail.error || detail.message || 'Request failed (' + response.status + ')');
  }
  return parsed as T;
}

export const api = {
  login: (identifier: string, password: string) =>
    call<AuthSession>('/auth/login', 'POST', { identifier, password }),
  register: (form: { username: string; email: string; firstName: string; lastName: string; phone: string; password: string }) =>
    call<AuthSession>('/auth/register', 'POST', { ...form, phone: form.phone || null }),
  me: (token: string) => call<Envelope<User>>('/users/me', 'GET', undefined, token).then(r => r.data),
  list: (token: string, params: URLSearchParams) =>
    call<Envelope<UserPage>>('/users/filter-search?' + params, 'GET', undefined, token).then(r => r.data),
  create: (token: string, form: UserInput) => call<Envelope<User>>('/users', 'POST', form, token).then(r => r.data),
  update: (token: string, id: string, form: UserInput) =>
    call<Envelope<User>>('/users/' + encodeURIComponent(id), 'PUT', form, token).then(r => r.data),
  setActive: (token: string, id: string, active: boolean) =>
    call<Envelope<User>>('/users/' + encodeURIComponent(id) + '/active', 'PATCH', { active }, token).then(r => r.data),
  remove: (token: string, id: string) =>
    call<Envelope<null>>('/users/' + encodeURIComponent(id), 'DELETE', undefined, token),
};
