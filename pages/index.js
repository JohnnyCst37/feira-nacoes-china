import { useState, useEffect } from 'react';
import { auth, provider } from '../lib/firebase';
import { signInWithPopup } from 'firebase/auth';

export default function Home() {
  const [stage, setStage] = useState('lanterns'); // lanterns -> dragon -> login

  useEffect(() => {
    const timer1 = setTimeout(() => setStage('dragon'), 5000);
    const timer2 = setTimeout(() => setStage('login'), 8000);
    return () => { clearTimeout(timer1); clearTimeout(timer2); };
  }, []);

  const handleLogin = async () => {
    try {
      await signInWithPopup(auth, provider);
      window.location.href = '/trilha';
    } catch (error) { console.error(error); }
  };

  return (
    <div className="h-screen w-full bg-black flex flex-col items-center justify-center overflow-hidden">
      {stage === 'lanterns' && (
        <div className="animate-pulse text-center">
          <h1 className="text-yellow-500 text-3xl font-serif">Festival das Lanternas</h1>
          <p className="text-red-600">Paz e Luz...</p>
        </div>
      )}

      {stage === 'dragon' && (
        <div className="relative w-full">
          <div className="dragon-anim text-5xl">🐉</div>
          <h1 className="text-red-500 text-4xl font-bold text-center">Ano Novo Chinês 2024</h1>
        </div>
      )}

      {stage === 'login' && (
        <button 
          onClick={handleLogin}
          className="bg-red-600 text-white px-8 py-3 rounded-full font-bold hover:bg-red-700 transition"
        >
          Entrar com Google
        </button>
      )}

      <style jsx>{`
        .dragon-anim {
          position: absolute;
          animation: moveDragon 3s linear forwards;
        }
        @keyframes moveDragon {
          0% { transform: translateX(-100%) translateY(0); }
          100% { transform: translateX(200%) translateY(-50px); }
        }
      `}</style>
    </div>
  );
}