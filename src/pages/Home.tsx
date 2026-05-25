import { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';
import { Sparkles, Compass, LogIn, CheckCircle2, Volume2, VolumeX } from 'lucide-react';
import { audio } from '../lib/audio';

type StageType = 'lanterns' | 'dragon' | 'login';

export default function Home() {
  const [stage, setStage] = useState<StageType>('lanterns');
  const [error, setError] = useState<string | null>(null);
  
  // Mock form state
  const [showMockForm, setShowMockForm] = useState(false);
  const [mockName, setMockName] = useState('');
  const [mockEmail, setMockEmail] = useState('');

  const { user, loginWithGoogle, loginWithMock, isMock } = useAuth();
  const navigate = useNavigate();

  const [isMuted, setIsMuted] = useState(true);

  useEffect(() => {
    setIsMuted(audio.getMuteState());
    audio.setMelodyUrl('/audio/traditional_chinese_music.webm');
    if (!audio.getMuteState()) {
      audio.playBackground();
    }
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

  // Se já estiver logado, redireciona
  useEffect(() => {
    if (user) {
      if (user.isAdmin) {
        navigate('/admin');
      } else {
        navigate('/trilha');
      }
    }
  }, [user, navigate]);

  useEffect(() => {
    const timer1 = setTimeout(() => setStage('dragon'), 5000);
    const timer2 = setTimeout(() => setStage('login'), 8000);
    return () => {
      clearTimeout(timer1);
      clearTimeout(timer2);
    };
  }, []);

  const handleLoginClick = async () => {
    setError(null);
    if (isMock) {
      setShowMockForm(true);
    } else {
      try {
        await loginWithGoogle();
      } catch (err: unknown) {
        console.error(err);
        setError("Erro ao tentar entrar com o Google. Por favor, tente novamente.");
      }
    }
  };

  const handleMockSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    if (!mockName.trim() || !mockEmail.trim()) {
      setError("Por favor, preencha todos os campos.");
      return;
    }
    try {
      await loginWithMock(mockName, mockEmail);
    } catch (err: unknown) {
      console.error(err);
      setError("Erro no login simulado.");
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

            {!showMockForm ? (
              <div className="space-y-4">
                <button
                  onClick={handleLoginClick}
                  className="group relative w-full flex items-center justify-center gap-3 bg-gradient-to-r from-chinese-red to-red-700 hover:from-red-600 hover:to-chinese-red text-white py-4 px-6 rounded-xl font-bold transition-all duration-300 shadow-[0_4px_20px_rgba(200,16,46,0.3)] hover:shadow-[0_4px_25px_rgba(200,16,46,0.5)] active:scale-95"
                >
                  <LogIn className="w-5 h-5 transition-transform group-hover:translate-x-1" />
                  <span>Entrar com o Google</span>
                  <div className="absolute inset-0 rounded-xl border border-chinese-gold/30 group-hover:border-chinese-gold/60 transition-colors pointer-events-none"></div>
                </button>

                {isMock && (
                  <p className="text-[10px] text-zinc-500 font-mono">
                    ℹ️ Modo Mock Ativo (Sem credenciais de banco)
                  </p>
                )}
              </div>
            ) : (
              <form onSubmit={handleMockSubmit} className="space-y-4 text-left animate-fade-in">
                <div className="space-y-1">
                  <label className="text-xs text-chinese-gold font-mono uppercase tracking-wider">Nome Completo</label>
                  <input
                    type="text"
                    required
                    value={mockName}
                    onChange={(e) => setMockName(e.target.value)}
                    placeholder="Ex: João Silva"
                    className="w-full bg-zinc-900 border border-zinc-700 rounded-xl py-3 px-4 text-white placeholder-zinc-500 focus:outline-none focus:border-chinese-gold transition"
                  />
                </div>

                <div className="space-y-1">
                  <label className="text-xs text-chinese-gold font-mono uppercase tracking-wider">E-mail</label>
                  <input
                    type="email"
                    required
                    value={mockEmail}
                    onChange={(e) => setMockEmail(e.target.value)}
                    placeholder="Ex: joao@escola.mt.gov.br"
                    className="w-full bg-zinc-900 border border-zinc-700 rounded-xl py-3 px-4 text-white placeholder-zinc-500 focus:outline-none focus:border-chinese-gold transition"
                  />
                  <p className="text-[10px] text-zinc-500 leading-normal mt-1">
                    💡 Dica: Use um e-mail contendo <code className="text-chinese-gold">professor</code> ou <code className="text-chinese-gold">admin</code> para logar no Painel do Professor.
                  </p>
                </div>

                <button
                  type="submit"
                  className="w-full bg-gradient-to-r from-chinese-gold to-yellow-600 hover:from-yellow-500 hover:to-chinese-gold text-black py-3 px-6 rounded-xl font-bold transition duration-300 flex items-center justify-center gap-2 active:scale-95 shadow-[0_4px_15px_rgba(255,215,0,0.2)]"
                >
                  <CheckCircle2 className="w-5 h-5" />
                  <span>Confirmar Login Simulado</span>
                </button>
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
