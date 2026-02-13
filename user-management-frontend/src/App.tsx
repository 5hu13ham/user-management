import {useEffect, useState} from 'react'
import { pingBackend } from "./api/backend";

function App() {
  const [status, setStatus] = useState("Checking Backend...")

  useEffect(() => {
    console.log("Calling backend /api/ping")
    fetch("http://localhost:8080/api/ping")
    .then((res) => {
      console.log("Raw response", res)
      return res.text()
  })
    .then((data) => {
      console.log("Backend says", data)
      return setStatus(data)
    })
    .catch((err) => setStatus("Backend Unreachable"))
  }, [])

  pingBackend()
  .then((data) => setStatus(data))
  .catch(() => setStatus("Backend Unreachable"));

  return (
     <div style={{ padding: "20px" }}>
      <h1>User Management Dashboard</h1>
      <p><strong> Status : </strong> {status}</p>
    </div>
  )
}

export default App
