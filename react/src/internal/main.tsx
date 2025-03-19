import { StrictMode, useEffect, useState } from 'react'
import { createRoot } from 'react-dom/client'
import App from '../App.tsx'
import { isAlive, startSerranoScript } from './serrano/serrano.ts'
import { Load } from './Load.tsx'

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <Main />
  </StrictMode>,
)


function Main() {
  var [isStarted, setStarted] = useState(false);
  useEffect(() => {
    let timer = setInterval(async () => {

      if (await isAlive()) {
        setStarted(true);
        clearInterval(timer);
      }
    }, 500)
  }, []);

  if (isStarted)
    return <App />
  else
    return <Load />

}