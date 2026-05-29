import { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';
import { Sparkles, Compass, LogIn, CheckCircle2, Volume2, VolumeX } from 'lucide-react';
import { audio } from '../lib/audio';
import { getAppSettings } from '../lib/db';

type StageType = 'lanterns' | 'dragon' | 'login';

export default function Home() {
  const [stage, setStage] = useState<StageType>('lanterns');
  const [error, setError] = useState<string | null>(null);
  
  // Anonymous / Mock form state
  const [showAnonForm, setShowAnonForm] = useState(false);
  const [studentName, setStudentName] = useState('');
  const [studentEmail, setStudentEmail] = useState('');

  const { user, loginWithGoogle, loginWithMock, loginAnonymously, isMock, logout } = useAuth();
  const navigate = useNavigate();

  const [isMuted, setIsMuted] = useState(true);

  useEffect(() => {
    const initAudio = async () => {
      try {
        const settings = await getAppSettings();
        audio.setMelodyUrl(settings.soundtrackUrl);
        audio.setVolume(settings.soundtrackVolume);
        
        setIsMuted(audio.getMuteState());
        if (!audio.getMuteState()) {
          audio.playBackground();
        }
      } catch (err) {
        console.error("Erro ao carregar configurações de áudio na Home:", err);
      }
    };

    initAudio();

    return () => {
      audio.pauseBackground();
    };
  }, []);

  const handleToggleMute = () => {
    const muted = audio.toggleMute();
    setIsMuted(muted);
    if (!muted) {
      audio.playBackground();
    } else {
      audio.pauseBackground();
    }
  };

  // Redirecionamento automático removido para permitir a livre visualização da Home pelo administrador e alunos logados

  useEffect(() => {
    const timer1 = setTimeout(() => setStage('dragon'), 5000);
    const timer2 = setTimeout(() => setStage('login'), 8000);
    return () => {
      clearTimeout(timer1);
      clearTimeout(timer2);
    };
  }, []);

  const handleGoogleLoginClick = async () => {
    setError(null);
    try {
      await loginWithGoogle();
    } catch (err: unknown) {
      console.error(err);
      setError("Erro ao tentar entrar com o Google. Por favor, tente novamente.");
    }
  };

  const handleFormSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    if (!studentName.trim()) {
      setError("Por favor, preencha seu nome.");
      return;
    }
    try {
      if (isMock) {
        await loginWithMock(studentName, studentEmail);
      } else {
        await loginAnonymously(studentName, studentEmail);
      }
    } catch (err: unknown) {
      console.error(err);
      setError("Erro ao autenticar. Por favor, tente novamente.");
    }
  };

  return (
    <div className="relative min-h-screen w-full bg-zinc-950 flex flex-col items-center justify-center overflow-hidden font-sans">
      {/* Video Background (Festival das Lanternas) */}
      <div className="absolute inset-0 w-full h-full overflow-hidden pointer-events-none z-0">
        <video
          autoPlay
          loop
          muted
          playsInline
          className="absolute inset-0 w-full h-full object-cover opacity-25 filter brightness-90 contrast-110"
        >
          <source src="/video/festival_lanternas.mp4" type="video/mp4" />
        </video>
        <div className="absolute inset-0 bg-gradient-to-b from-black/50 via-zinc-950/75 to-zinc-950"></div>
      </div>

      {/* Mute/Unmute da Trilha de Fundo na Home */}
      <div className="absolute top-4 right-4 z-20">
        <button
          onClick={handleToggleMute}
          className="p-3 bg-zinc-900/85 hover:bg-zinc-800 text-chinese-gold border border-zinc-800 rounded-xl transition active:scale-95 shadow-lg backdrop-blur-sm"
          title={isMuted ? "Tocar Música de Fundo" : "Mutar Música"}
        >
          {isMuted ? <VolumeX className="w-5 h-5" /> : <Volume2 className="w-5 h-5 animate-bounce" />}
        </button>
      </div>

      {/* Background Glow effects (Ghost Aesthetic) */}
      <div className="absolute top-1/4 left-1/4 w-96 h-96 bg-chinese-red/5 rounded-full blur-3xl animate-pulse-slow z-0"></div>
      <div className="absolute bottom-1/4 right-1/4 w-96 h-96 bg-chinese-gold/5 rounded-full blur-3xl animate-pulse-slow z-0" style={{ animationDelay: '1.5s' }}></div>

      {/* Stage: Lanterns */}
      {stage === 'lanterns' && (
        <div className="z-10 text-center animate-fade-in flex flex-col items-center gap-4 px-6">
          <div className="relative">
            <Compass className="w-16 h-16 text-chinese-gold animate-spin-slow drop-shadow-[0_0_15px_rgba(255,215,0,0.5)]" />
            <Sparkles className="w-6 h-6 text-chinese-red absolute -top-2 -right-2 animate-bounce" />
          </div>
          <h1 className="text-chinese-gold text-4xl sm:text-5xl font-serif tracking-widest uppercase border-b border-chinese-gold/30 pb-3">
            Festival das Lanternas
          </h1>
          <p className="text-chinese-red/80 font-medium text-lg tracking-wide animate-pulse">
            Paz, Harmonia e Sabedoria...
          </p>
        </div>
      )}

      {/* Stage: Dragon */}
      {stage === 'dragon' && (
        <div className="z-10 relative w-full flex flex-col items-center justify-center px-6">
          <div className="dragon-anim text-7xl select-none filter drop-shadow-[0_0_20px_rgba(199,16,46,0.6)]">
            🐉
          </div>
          <h1 className="text-transparent bg-clip-text bg-gradient-to-r from-chinese-red to-chinese-gold text-4xl sm:text-6xl font-bold font-serif tracking-widest text-center mt-8 animate-fade-in">
            Ano Novo Chinês 2024
          </h1>
          <p className="text-zinc-500 text-sm mt-2 font-mono tracking-widest">
            A ERA DO DRAGÃO DE MADEIRA
          </p>
        </div>
      )}

      {/* Stage: Login */}
      {stage === 'login' && (
        <div className="z-10 flex flex-col items-center gap-8 w-full max-w-md px-6 text-center animate-fade-in">
          <div className="space-y-3">
            <h2 className="text-chinese-gold text-5xl font-serif tracking-wider">
              Feira das Nações
            </h2>
            <p className="text-zinc-400 text-sm tracking-widest uppercase">
              Jornada sobre a Cultura Chinesa
            </p>
          </div>

          <div className="w-full bg-chinese-gray/80 border border-chinese-gold/20 rounded-2xl p-8 backdrop-blur-md shadow-2xl space-y-6">
            <p className="text-zinc-300 text-base leading-relaxed">
              Bem-vindo, Viajante. Identifique-se para iniciar sua trilha pelo conhecimento milenar.
            </p>

            {error && (
              <div className="bg-red-950/50 border border-red-500/30 text-red-400 p-3 rounded-lg text-sm">
                {error}
              </div>
            )}

            {user ? (
              <div className="space-y-5 animate-scale-up">
                <div className="text-zinc-300 text-sm font-sans space-y-1">
                  <p>Olá, <span className="text-chinese-gold font-bold">{user.displayName || 'Viajante'}</span>!</p>
                  <p className="text-xs text-zinc-400">Você já está identificado na jornada.</p>
                </div>
                
                <button
                  onClick={() => navigate(user.isAdmin ? '/admin' : '/trilha')}
                  className="w-full flex items-center justify-center gap-3 bg-gradient-to-r from-chinese-gold to-yellow-600 hover:from-yellow-500 hover:to-chinese-gold text-black py-3.5 px-6 rounded-xl font-bold transition-all duration-300 shadow-[0_4px_20px_rgba(255,215,0,0.25)] active:scale-95"
                >
                  <Compass className="w-5 h-5 animate-spin-slow" />
                  <span>{user.isAdmin ? "Ir para o Painel do Professor" : "Acessar a Trilha do Quiz"}</span>
                </button>

                <button
                  onClick={async () => {
                    await logout();
                    setShowAnonForm(false);
                  }}
                  className="text-[10px] text-zinc-550 hover:text-red-400 transition font-mono uppercase tracking-widest block mx-auto pt-2 hover:underline"
                >
                  Sair / Entrar com outra conta 🚪
                </button>
              </div>
            ) : !showAnonForm ? (
              <div className="space-y-4">
                {/* Botão Google */}
                <button
                  onClick={handleGoogleLoginClick}
                  className="group relative w-full flex items-center justify-center gap-3 bg-gradient-to-r from-chinese-red to-red-700 hover:from-red-600 hover:to-chinese-red text-white py-4 px-6 rounded-xl font-bold transition-all duration-300 shadow-[0_4px_20px_rgba(200,16,46,0.3)] hover:shadow-[0_4px_25px_rgba(200,16,46,0.5)] active:scale-95"
                >
                  <LogIn className="w-5 h-5 transition-transform group-hover:translate-x-1" />
                  <span>Entrar com o Google</span>
                  <div className="absolute inset-0 rounded-xl border border-chinese-gold/30 group-hover:border-chinese-gold/60 transition-colors pointer-events-none"></div>
                </button>

                {/* Botão Acesso Rápido */}
                <button
                  onClick={() => setShowAnonForm(true)}
                  className="group relative w-full flex items-center justify-center gap-3 bg-zinc-900 hover:bg-zinc-800 text-chinese-gold py-4 px-6 rounded-xl font-bold transition-all duration-300 border border-zinc-800 hover:border-chinese-gold/30 active:scale-95"
                >
                  <Sparkles className="w-5 h-5 text-chinese-gold animate-pulse" />
                  <span>Acesso Rápido (Sem Conta Google)</span>
                </button>

                {isMock && (
                  <p className="text-[10px] text-zinc-550 font-mono">
                    ℹ️ Modo Mock Ativo (Sem credenciais de banco)
                  </p>
                )}
              </div>
            ) : (
              <form onSubmit={handleFormSubmit} className="space-y-4 text-left animate-fade-in">
                <div className="space-y-1">
                  <label className="text-xs text-chinese-gold font-mono uppercase tracking-wider">Nome Completo</label>
                  <input
                    type="text"
                    required
                    value={studentName}
                    onChange={(e) => setStudentName(e.target.value)}
                    placeholder="Ex: João Silva"
                    className="w-full bg-zinc-900 border border-zinc-700 rounded-xl py-3 px-4 text-white placeholder-zinc-500 focus:outline-none focus:border-chinese-gold transition"
                  />
                </div>

                <div className="space-y-1">
                  <label className="text-xs text-chinese-gold font-mono uppercase tracking-wider">E-mail ou Turma (Opcional)</label>
                  <input
                    type="text"
                    value={studentEmail}
                    onChange={(e) => setStudentEmail(e.target.value)}
                    placeholder="Ex: 7º Ano D ou joao@escola.mt.gov.br"
                    className="w-full bg-zinc-900 border border-zinc-700 rounded-xl py-3 px-4 text-white placeholder-zinc-500 focus:outline-none focus:border-chinese-gold transition"
                  />
                  <p className="text-[10px] text-zinc-500 leading-normal mt-1">
                    💡 Dica: Se você for o Professor, volte e use "Entrar com o Google" para ter acesso administrativo.
                  </p>
                </div>

                <div className="flex gap-3 pt-2">
                  <button
                    type="button"
                    onClick={() => setShowAnonForm(false)}
                    className="flex-1 bg-zinc-900 hover:bg-zinc-800 border border-zinc-800 text-zinc-400 py-3 px-4 rounded-xl font-bold transition duration-300 text-xs active:scale-95"
                  >
                    Voltar
                  </button>
                  <button
                    type="submit"
                    className="flex-1 bg-gradient-to-r from-chinese-gold to-yellow-600 hover:from-yellow-500 hover:to-chinese-gold text-black py-3 px-4 rounded-xl font-bold transition duration-300 flex items-center justify-center gap-2 text-xs active:scale-95 shadow-[0_4px_15px_rgba(255,215,0,0.2)]"
                  >
                    <CheckCircle2 className="w-4 h-4" />
                    <span>Iniciar Jornada</span>
                  </button>
                </div>
              </form>
            )}
          </div>

          <div className="text-xs text-zinc-600 font-mono tracking-widest uppercase mt-4">
            Escolas Públicas de MT &copy; 2026
          </div>
        </div>
      )}
    </div>
  );
}
