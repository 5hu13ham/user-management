export const BACKEND_URL = "http://localhost:8080";

export function pingBackend(){
  return fetch(`${BACKEND_URL}/api/ping`)
  .then((res) => {
    if(!res.ok){
      throw new Error("Backend Error")
    }
    return res.text();
  })
}