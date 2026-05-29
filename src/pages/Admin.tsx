import { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';
import { 
  getStudentsProgress, 
  getQuestions, 
  saveQuestion, 
  getRewardVideos, 
  saveRewardVideos, 
  getStationConfigs, 
  saveStationConfig, 
  getAppSettings,
  saveAppSettings,
  deleteStudentProgress,
  QuestionCard, 
  StudentProgress, 
  StationConfig
} from '../lib/db';
import { Users, ClipboardList, Star, Save, LogOut, ChevronLeft, Check, Play, Square, Volume2, Trash2 } from 'lucide-react';
import { audio } from '../lib/audio';

const getBadgeEmojis = (badgesList: string[] = []) => {
  const map: Record<string, string> = {
    badge_1: '🐉',
    badge_2: '🏮',
    badge_3: '🎵',
    badge_4: '🦁',
    badge_5: '⚔️',
    badge_final: '👑'
  };
  return badgesList.map(b => map[b] || '').filter(Boolean).join(' ');
};

export default function Admin() {
  const { logout } = useAuth();
  const navigate = useNavigate();

  const [students, setStudents] = useState<StudentProgress[]>([]);
  const [questions, setQuestions] = useState<QuestionCard[]>([]);
  const [loading, setLoading] = useState(true);
  
  // Ingestion form state
  const [editingGroupId, setEditingGroupId] = useState<number>(1);
  const [theme, setTheme] = useState('');
  const [questionText, setQuestionText] = useState('');
  const [opt1, setOpt1] = useState('');
  const [opt2, setOpt2] = useState('');
  const [opt3, setOpt3] = useState('');
  const [opt4, setOpt4] = useState('');
  const [correctAnswer, setCorrectAnswer] = useState('');
  const [videoUrl, setVideoUrl] = useState('');
  const [explanationText, setExplanationText] = useState('');
  
  const [saveSuccess, setSaveSuccess] = useState(false);

  // Vídeos de recompensa final
  const [rewardUrls, setRewardUrls] = useState<string[]>(['', '', '', '']);
  const [rewardSaveSuccess, setRewardSaveSuccess] = useState(false);

  // Atmosfera das Estações (StationConfig)
  const [stationConfigs, setStationConfigs] = useState<StationConfig[]>([]);
  const [editingStationId, setEditingStationId] = useState<number>(1);
  const [stationBgImage, setStationBgImage] = useState('');
  const [stationBgVideo, setStationBgVideo] = useState('');
  const [stationMelody, setStationMelody] = useState('');
  const [stationTitle, setStationTitle] = useState('');
  const [stationSubtitle, setStationSubtitle] = useState('');
  const [stationExtraMedia1, setStationExtraMedia1] = useState('');
  const [stationExtraMedia2, setStationExtraMedia2] = useState('');
  const [stationSaveSuccess, setStationSaveSuccess] = useState(false);

  // Trilha Sonora Global
  const [soundtrackUrl, setSoundtrackUrl] = useState('');
  const [soundtrackVolume, setSoundtrackVolume] = useState(0.25);
  const [soundtrackSaveSuccess, setSoundtrackSaveSuccess] = useState(false);
  const [isPreviewPlaying, setIsPreviewPlaying] = useState(false);

  const loadAdminData = async () => {
    setLoading(true);
    try {
      const [studentList, questionList, rewardList, configList, appSettings] = await Promise.all([
        getStudentsProgress(),
        getQuestions(),
        getRewardVideos(),
        getStationConfigs(),
        getAppSettings()
      ]);
      setStudents(studentList);
      setQuestions(questionList);
      setRewardUrls(rewardList);
      setStationConfigs(configList);
      setSoundtrackUrl(appSettings.soundtrackUrl);
      setSoundtrackVolume(appSettings.soundtrackVolume);
      
      // Auto-popula o form com a pergunta do grupo 1 por padrão
      const q1 = questionList.find(q => q.id_grupo === 1);
      if (q1) {
        populateForm(q1);
      }

      // Auto-popula a config da estação 1 por padrão
      const c1 = configList.find(c => c.stationId === 1);
      if (c1) {
        populateStationConfigForm(c1);
      }
    } catch (error) {
      console.error("Erro ao carregar dados do admin:", error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    audio.pauseBackground(); // Garante silêncio inicial no painel admin
    loadAdminData();
    return () => {
      audio.stopAll();
    };
  }, []);

  const populateForm = (q: QuestionCard) => {
    setEditingGroupId(q.id_grupo);
    setTheme(q.tema);
    setQuestionText(q.pergunta);
    setOpt1(q.opcoes[0] || '');
    setOpt2(q.opcoes[1] || '');
    setOpt3(q.opcoes[2] || '');
    setOpt4(q.opcoes[3] || '');
    setCorrectAnswer(q.resposta_correta);
    setVideoUrl(q.video_feedback_url);
    setExplanationText(q.texto_explicativo || '');
  };

  const handleGroupSelect = (idGrupo: number) => {
    setEditingGroupId(idGrupo);
    const q = questions.find(item => item.id_grupo === idGrupo);
    if (q) {
      populateForm(q);
    } else {
      // Limpa para novo cadastro do grupo
      setTheme('');
      setQuestionText('');
      setOpt1('');
      setOpt2('');
      setOpt3('');
      setOpt4('');
      setCorrectAnswer('');
      setVideoUrl('');
      setExplanationText('');
    }
  };

  const populateStationConfigForm = (c: StationConfig) => {
    setEditingStationId(c.stationId);
    setStationBgImage(c.backgroundImageUrl || '');
    setStationBgVideo(c.backgroundVideoUrl || '');
    setStationMelody(c.melodyUrl || '');
    setStationTitle(c.customTitle || '');
    setStationSubtitle(c.customSubtitle || '');
    setStationExtraMedia1(c.extraMediaUrl1 || '');
    setStationExtraMedia2(c.extraMediaUrl2 || '');
  };

  const handleStationSelect = (stationId: number) => {
    setEditingStationId(stationId);
    const c = stationConfigs.find(item => item.stationId === stationId);
    if (c) {
      populateStationConfigForm(c);
    } else {
      setStationBgImage('');
      setStationBgVideo('');
      setStationMelody('');
      setStationTitle('');
      setStationSubtitle('');
      setStationExtraMedia1('');
      setStationExtraMedia2('');
    }
  };

  const handleSaveStationConfig = async (e: React.FormEvent) => {
    e.preventDefault();
    setStationSaveSuccess(false);

    const configData: StationConfig = {
      stationId: editingStationId,
      backgroundImageUrl: stationBgImage.trim(),
      backgroundVideoUrl: getEmbedUrl(stationBgVideo.trim()),
      melodyUrl: stationMelody.trim(),
      customTitle: stationTitle.trim(),
      customSubtitle: stationSubtitle.trim(),
      extraMediaUrl1: getEmbedUrl(stationExtraMedia1.trim()),
      extraMediaUrl2: getEmbedUrl(stationExtraMedia2.trim())
    };

    try {
      await saveStationConfig(configData);
      setStationSaveSuccess(true);
      setTimeout(() => setStationSaveSuccess(false), 3000);
      
      // Atualiza localmente
      const updatedConfigs = await getStationConfigs();
      setStationConfigs(updatedConfigs);
    } catch (err) {
      console.error("Erro ao salvar config da estação:", err);
      alert("Erro ao salvar configurações de atmosfera.");
    }
  };

  const getEmbedUrl = (url: string): string => {
    if (!url) return '';
    
    // YouTube watch or share links (e.g. watch?v=ID, shorts/ID, youtu.be/ID)
    let match = url.match(/(?:youtube\.com\/watch\?v=|youtu\.be\/|youtube\.com\/embed\/|youtube\.com\/shorts\/)([a-zA-Z0-9_-]+)/);
    if (match && match[1]) {
      return `https://www.youtube.com/embed/${match[1]}`;
    }
    
    // Google Drive links (e.g. /file/d/ID/view)
    match = url.match(/drive\.google\.com\/file\/d\/([a-zA-Z0-9_-]+)/);
    if (match && match[1]) {
      return `https://drive.google.com/file/d/${match[1]}/preview`;
    }
    
    return url;
  };

  const handleSaveQuestion = async (e: React.FormEvent) => {
    e.preventDefault();
    setSaveSuccess(false);

    if (!correctAnswer || ![opt1, opt2, opt3, opt4].includes(correctAnswer)) {
      alert("A resposta correta precisa coincidir exatamente com uma das 4 opções criadas.");
      return;
    }

    if (!videoUrl.trim() && !explanationText.trim()) {
      alert("Por favor, forneça o Link do Vídeo ou o Texto Explicativo (ou ambos) para o feedback do grupo.");
      return;
    }

    const questionData: QuestionCard = {
      id: `grupo_${editingGroupId}`,
      id_grupo: editingGroupId,
      tema: theme.trim(),
      pergunta: questionText.trim(),
      opcoes: [opt1.trim(), opt2.trim(), opt3.trim(), opt4.trim()],
      resposta_correta: correctAnswer.trim(),
      video_feedback_url: getEmbedUrl(videoUrl.trim()),
      texto_explicativo: explanationText.trim()
    };

    try {
      await saveQuestion(questionData);
      setSaveSuccess(true);
      setTimeout(() => setSaveSuccess(false), 3000);
      
      // Atualiza a lista local de perguntas
      const updatedQuestions = await getQuestions();
      setQuestions(updatedQuestions);
    } catch (err) {
      console.error("Erro ao salvar pergunta:", err);
      alert("Houve um erro ao tentar salvar no banco de dados.");
    }
  };

  const handleSaveRewardVideos = async (e: React.FormEvent) => {
    e.preventDefault();
    setRewardSaveSuccess(false);

    // Converte e limpa cada URL
    const cleanedUrls = rewardUrls.map(url => getEmbedUrl(url.trim()));
    
    // Verifica se pelo menos uma URL está preenchida
    if (cleanedUrls.filter(Boolean).length === 0) {
      alert("Por favor, preencha pelo menos um vídeo de premiação.");
      return;
    }

    try {
      await saveRewardVideos(cleanedUrls);
      setRewardSaveSuccess(true);
      setTimeout(() => setRewardSaveSuccess(false), 3000);
      
      // Recarrega os dados
      const updatedRewards = await getRewardVideos();
      setRewardUrls(updatedRewards);
    } catch (err) {
      console.error("Erro ao salvar vídeos de premiação:", err);
      alert("Erro ao salvar vídeos de premiação no banco.");
    }
  };

  const handleTogglePreview = () => {
    if (isPreviewPlaying) {
      audio.pauseBackground();
      setIsPreviewPlaying(false);
    } else {
      audio.setMelodyUrl(soundtrackUrl);
      audio.setVolume(soundtrackVolume);
      audio.playBackground();
      setIsPreviewPlaying(true);
    }
  };

  const handleSaveSoundtrack = async (e: React.FormEvent) => {
    e.preventDefault();
    setSoundtrackSaveSuccess(false);

    try {
      await saveAppSettings({
        soundtrackUrl: soundtrackUrl.trim(),
        soundtrackVolume: soundtrackVolume
      });
      setSoundtrackSaveSuccess(true);
      setTimeout(() => setSoundtrackSaveSuccess(false), 3000);
    } catch (err) {
      console.error("Erro ao salvar trilha sonora global:", err);
      alert("Erro ao salvar trilha sonora.");
    }
  };

  const handleLogout = async () => {
    await logout();
    navigate('/');
  };

  const handleDeleteStudent = async (uid: string, name: string) => {
    if (window.confirm(`Deseja realmente excluir o progresso do aluno "${name}"? Esta ação removerá o registro permanentemente do banco de dados.`)) {
      try {
        await deleteStudentProgress(uid);
        // Recarrega os dados localmente
        await loadAdminData();
      } catch (err) {
        console.error("Erro ao deletar progresso do aluno:", err);
        alert("Erro ao tentar excluir o progresso do aluno.");
      }
    }
  };

  // Cálculos de métricas
  const totalStudents = students.length;
  const completedCount = students.filter(s => s.completed).length;
  const averageRating = totalStudents > 0 
    ? (students.reduce((acc, curr) => acc + curr.stars, 0) / students.filter(s => s.stars > 0).length || 0).toFixed(1)
    : "0.0";

  return (
    <div className="min-h-screen w-full bg-chinese-dark text-white flex flex-col font-sans">
      {/* Header */}
      <header className="border-b border-chinese-gold/20 bg-zinc-950/80 backdrop-blur-md sticky top-0 z-20 px-6 py-4 flex items-center justify-between">
        <div className="flex items-center gap-3">
          <ChevronLeft 
            className="w-6 h-6 text-zinc-400 hover:text-chinese-gold cursor-pointer transition" 
            onClick={() => navigate('/')} 
          />
          <h1 className="font-serif tracking-wider text-chinese-gold text-lg sm:text-xl">
            Painel do Professor - Administração
          </h1>
        </div>
        <button 
          onClick={handleLogout}
          className="flex items-center gap-2 text-zinc-400 hover:text-chinese-red text-sm transition font-medium"
        >
          <LogOut className="w-4 h-4" />
          <span>Sair</span>
        </button>
      </header>

      {loading ? (
        <div className="flex-1 flex items-center justify-center">
          <p className="text-zinc-500 font-mono tracking-widest animate-pulse">CARREGANDO DADOS DO PAINEL...</p>
        </div>
      ) : (
        <main className="flex-1 max-w-7xl w-full mx-auto p-6 space-y-8">
          
          {/* Métricas Globais */}
          <section className="grid grid-cols-1 md:grid-cols-3 gap-6">
            <div className="bg-chinese-gray/60 border border-zinc-800 rounded-2xl p-6 flex items-center gap-4">
              <div className="w-12 h-12 bg-blue-500/10 text-blue-400 rounded-xl flex items-center justify-center border border-blue-500/20">
                <Users className="w-6 h-6" />
              </div>
              <div>
                <p className="text-xs text-zinc-500 font-mono uppercase tracking-wider">Alunos Logados</p>
                <h3 className="text-3xl font-serif font-bold text-zinc-100">{totalStudents}</h3>
              </div>
            </div>

            <div className="bg-chinese-gray/60 border border-zinc-800 rounded-2xl p-6 flex items-center gap-4">
              <div className="w-12 h-12 bg-green-500/10 text-green-400 rounded-xl flex items-center justify-center border border-green-500/20">
                <ClipboardList className="w-6 h-6" />
              </div>
              <div>
                <p className="text-xs text-zinc-500 font-mono uppercase tracking-wider">Trilhas Concluídas</p>
                <h3 className="text-3xl font-serif font-bold text-zinc-100">{completedCount}</h3>
              </div>
            </div>

            <div className="bg-chinese-gray/60 border border-zinc-800 rounded-2xl p-6 flex items-center gap-4">
              <div className="w-12 h-12 bg-chinese-gold/10 text-chinese-gold rounded-xl flex items-center justify-center border border-chinese-gold/20">
                <Star className="w-6 h-6 fill-chinese-gold" />
              </div>
              <div>
                <p className="text-xs text-zinc-500 font-mono uppercase tracking-wider">Avaliação da Feira</p>
                <h3 className="text-3xl font-serif font-bold text-chinese-gold">{averageRating} / 5.0</h3>
              </div>
            </div>
          </section>

          {/* Grid Principal Form / Alunos */}
          <div className="grid grid-cols-1 lg:grid-cols-12 gap-8">
            
            {/* Wrapper Esquerdo para os Formulários (5 Colunas) */}
            <div className="lg:col-span-5 space-y-8">
              
              {/* Formulário de Ingestão de Trabalhos */}
              <section className="bg-zinc-950/80 border border-zinc-800 rounded-2xl p-6 space-y-6">
              <div>
                <h2 className="text-xl font-serif text-chinese-gold tracking-wide">Cadastro de Trabalhos</h2>
                <p className="text-zinc-500 text-xs mt-1">
                  Gerencie as perguntas do quiz e associe os vídeos produzidos pelos alunos.
                </p>
              </div>

              {/* Botões seletores de Grupos */}
              <div className="space-y-2">
                <label className="text-xs text-zinc-400 font-mono uppercase tracking-wider">Selecione o Grupo do Aluno:</label>
                <div className="flex gap-2">
                  {[1, 2, 3, 4, 5].map((g) => {
                    const hasQuestion = questions.some(q => q.id_grupo === g);
                    return (
                      <button
                        key={g}
                        type="button"
                        onClick={() => handleGroupSelect(g)}
                        className={`flex-1 py-2.5 rounded-lg font-bold text-sm transition-all border ${
                          editingGroupId === g
                            ? 'bg-chinese-red border-chinese-red text-white shadow-md'
                            : hasQuestion
                              ? 'bg-zinc-900 border-zinc-800 text-chinese-gold hover:border-chinese-gold/50'
                              : 'bg-zinc-900/50 border-zinc-900 text-zinc-600 border-dashed hover:border-zinc-700'
                        }`}
                      >
                        G{g}
                      </button>
                    );
                  })}
                </div>
              </div>

              {/* Formulário Ingestão */}
              <form onSubmit={handleSaveQuestion} className="space-y-4">
                <div className="space-y-1">
                  <label className="text-xs text-zinc-400 font-mono uppercase tracking-wider block">Tema do Trabalho</label>
                  <input
                    type="text"
                    required
                    value={theme}
                    onChange={(e) => setTheme(e.target.value)}
                    placeholder="Ex: Medicina Tradicional"
                    className="w-full bg-zinc-900 border border-zinc-800 rounded-xl py-2.5 px-4 text-white text-sm focus:outline-none focus:border-chinese-gold transition"
                  />
                </div>

                <div className="space-y-1">
                  <label className="text-xs text-zinc-400 font-mono uppercase tracking-wider block">Pergunta do Quiz</label>
                  <textarea
                    rows={2}
                    required
                    value={questionText}
                    onChange={(e) => setQuestionText(e.target.value)}
                    placeholder="Qual a influência milenar da..."
                    className="w-full bg-zinc-900 border border-zinc-800 rounded-xl py-2.5 px-4 text-white text-sm focus:outline-none focus:border-chinese-gold transition resize-none"
                  />
                </div>

                {/* Opções */}
                <div className="space-y-2">
                  <label className="text-xs text-zinc-400 font-mono uppercase tracking-wider block">Alternativas de Resposta</label>
                  <input
                    type="text"
                    required
                    value={opt1}
                    onChange={(e) => setOpt1(e.target.value)}
                    placeholder="Opção A"
                    className="w-full bg-zinc-900 border border-zinc-800 rounded-xl py-2 px-3 text-white text-xs focus:outline-none focus:border-chinese-gold transition"
                  />
                  <input
                    type="text"
                    required
                    value={opt2}
                    onChange={(e) => setOpt2(e.target.value)}
                    placeholder="Opção B"
                    className="w-full bg-zinc-900 border border-zinc-800 rounded-xl py-2 px-3 text-white text-xs focus:outline-none focus:border-chinese-gold transition"
                  />
                  <input
                    type="text"
                    required
                    value={opt3}
                    onChange={(e) => setOpt3(e.target.value)}
                    placeholder="Opção C"
                    className="w-full bg-zinc-900 border border-zinc-800 rounded-xl py-2 px-3 text-white text-xs focus:outline-none focus:border-chinese-gold transition"
                  />
                  <input
                    type="text"
                    required
                    value={opt4}
                    onChange={(e) => setOpt4(e.target.value)}
                    placeholder="Opção D"
                    className="w-full bg-zinc-900 border border-zinc-800 rounded-xl py-2 px-3 text-white text-xs focus:outline-none focus:border-chinese-gold transition"
                  />
                </div>

                <div className="space-y-1">
                  <label className="text-xs text-zinc-400 font-mono uppercase tracking-wider block">Resposta Correta</label>
                  <input
                    type="text"
                    required
                    value={correctAnswer}
                    onChange={(e) => setCorrectAnswer(e.target.value)}
                    placeholder="Escreva igual a uma das opções"
                    className="w-full bg-zinc-900 border border-zinc-800 rounded-xl py-2.5 px-4 text-white text-sm focus:outline-none focus:border-chinese-gold transition"
                  />
                </div>

                <div className="space-y-1">
                  <label className="text-xs text-zinc-400 font-mono uppercase tracking-wider block">Link do Vídeo (YouTube, Shorts ou Google Drive)</label>
                  <input
                    type="url"
                    value={videoUrl}
                    onChange={(e) => setVideoUrl(e.target.value)}
                    placeholder="Ex: https://www.youtube.com/watch?v=..."
                    className="w-full bg-zinc-900 border border-zinc-800 rounded-xl py-2.5 px-4 text-white text-sm focus:outline-none focus:border-chinese-gold transition"
                  />
                  <p className="text-[10px] text-zinc-500 font-mono mt-0.5">
                    💡 Links de vídeos normais do YouTube ou compartilhados do Google Drive são formatados automaticamente.
                  </p>
                </div>

                <div className="space-y-1">
                  <label className="text-xs text-zinc-400 font-mono uppercase tracking-wider block">Texto Explicativo (Card Escrito Opcional)</label>
                  <textarea
                    rows={3}
                    value={explanationText}
                    onChange={(e) => setExplanationText(e.target.value)}
                    placeholder="Conteúdo extra em texto que os alunos prepararam como feedback..."
                    className="w-full bg-zinc-900 border border-zinc-800 rounded-xl py-2.5 px-4 text-white text-sm focus:outline-none focus:border-chinese-gold transition resize-none"
                  />
                </div>

                <button
                  type="submit"
                  className="w-full bg-gradient-to-r from-chinese-gold to-yellow-600 hover:from-yellow-500 hover:to-chinese-gold text-black py-3 rounded-xl font-bold flex items-center justify-center gap-2 active:scale-95 shadow-md transition"
                >
                  {saveSuccess ? <Check className="w-5 h-5" /> : <Save className="w-5 h-5" />}
                  <span>{saveSuccess ? 'Salvo com Sucesso!' : 'Salvar Pergunta & Feedback'}</span>
                </button>
              </form>
            </section>

            {/* Nova Seção: Vídeos de Premiação Final */}
            <section className="bg-zinc-950/80 border border-zinc-800 rounded-2xl p-6 space-y-4 animate-fade-in">
              <div>
                <h2 className="text-xl font-serif text-chinese-gold tracking-wide">Vídeos de Premiação (Final)</h2>
                <p className="text-zinc-500 text-xs mt-1">
                  Cadastre as 4 opções de vídeos sobre marcos da cultura chinesa. Um deles será sorteado para o aluno ao concluir a trilha.
                </p>
              </div>

              <form onSubmit={handleSaveRewardVideos} className="space-y-4">
                {[0, 1, 2, 3].map((idx) => (
                  <div key={idx} className="space-y-1">
                    <label className="text-[10px] text-zinc-400 font-mono uppercase tracking-wider block">Vídeo de Recompensa {idx + 1} (YouTube)</label>
                    <input
                      type="url"
                      value={rewardUrls[idx] || ''}
                      onChange={(e) => {
                        const newUrls = [...rewardUrls];
                        newUrls[idx] = e.target.value;
                        setRewardUrls(newUrls);
                      }}
                      placeholder="Ex: https://www.youtube.com/watch?v=..."
                      className="w-full bg-zinc-900 border border-zinc-800 rounded-xl py-2 px-3 text-white text-xs focus:outline-none focus:border-chinese-gold transition"
                    />
                  </div>
                ))}

                <button
                  type="submit"
                  className="w-full bg-gradient-to-r from-chinese-gold to-yellow-600 hover:from-yellow-500 hover:to-chinese-gold text-black py-2.5 rounded-xl font-bold flex items-center justify-center gap-2 active:scale-95 shadow-md transition text-xs"
                >
                  {rewardSaveSuccess ? <Check className="w-4 h-4" /> : <Save className="w-4 h-4" />}
                  <span>{rewardSaveSuccess ? 'Vídeos Salvos!' : 'Salvar 4 Vídeos de Recompensa'}</span>
                </button>
              </form>
            </section>

            {/* Nova Seção: Trilha Sonora Global */}
            <section className="bg-zinc-950/80 border border-zinc-800 rounded-2xl p-6 space-y-4 animate-fade-in">
              <div>
                <h2 className="text-xl font-serif text-chinese-gold tracking-wide">Trilha Sonora Global</h2>
                <p className="text-zinc-500 text-xs mt-1">
                  Defina o áudio MP3 de fundo do aplicativo e regule o volume padrão ouvido pelos visitantes.
                </p>
              </div>

              <form onSubmit={handleSaveSoundtrack} className="space-y-4">
                <div className="space-y-1">
                  <label className="text-xs text-zinc-400 font-mono uppercase tracking-wider block">URL da Música (MP3 / WebM)</label>
                  <input
                    type="text"
                    value={soundtrackUrl}
                    onChange={(e) => setSoundtrackUrl(e.target.value)}
                    placeholder="Deixe em branco para o padrão ou cole um link MP3"
                    className="w-full bg-zinc-900 border border-zinc-800 rounded-xl py-2.5 px-4 text-white text-sm focus:outline-none focus:border-chinese-gold transition"
                  />
                </div>

                <div className="space-y-2">
                  <div className="flex justify-between items-center">
                    <label className="text-xs text-zinc-400 font-mono uppercase tracking-wider">Volume da Música</label>
                    <span className="text-xs text-chinese-gold font-mono font-bold">
                      {Math.round(soundtrackVolume * 100)}%
                    </span>
                  </div>
                  <div className="flex items-center gap-3">
                    <Volume2 className="w-4 h-4 text-zinc-500" />
                    <input
                      type="range"
                      min="0"
                      max="1"
                      step="0.05"
                      value={soundtrackVolume}
                      onChange={(e) => {
                        const val = parseFloat(e.target.value);
                        setSoundtrackVolume(val);
                        audio.setVolume(val); // Ajusta o volume do elemento se estiver tocando
                      }}
                      className="flex-1 h-1.5 bg-zinc-800 rounded-lg appearance-none cursor-pointer accent-chinese-gold"
                    />
                  </div>
                </div>

                <div className="flex gap-2 pt-2">
                  <button
                    type="button"
                    onClick={handleTogglePreview}
                    className="flex-1 flex items-center justify-center gap-2 bg-zinc-900 hover:bg-zinc-800 border border-zinc-800 text-zinc-300 py-2.5 rounded-xl text-xs font-bold active:scale-95 transition"
                  >
                    {isPreviewPlaying ? (
                      <>
                        <Square className="w-3.5 h-3.5 text-chinese-red fill-chinese-red" />
                        <span>Parar Teste</span>
                      </>
                    ) : (
                      <>
                        <Play className="w-3.5 h-3.5 text-chinese-gold fill-chinese-gold" />
                        <span>Testar Trilha</span>
                      </>
                    )}
                  </button>

                  <button
                    type="submit"
                    className="flex-1 bg-gradient-to-r from-chinese-gold to-yellow-600 hover:from-yellow-500 hover:to-chinese-gold text-black py-2.5 rounded-xl font-bold flex items-center justify-center gap-2 active:scale-95 shadow-md transition text-xs"
                  >
                    {soundtrackSaveSuccess ? <Check className="w-4 h-4" /> : <Save className="w-4 h-4" />}
                    <span>{soundtrackSaveSuccess ? 'Configurações Salvas!' : 'Salvar Trilha & Volume'}</span>
                  </button>
                </div>
              </form>
            </section>

            {/* Nova Seção: Configurações de Atmosfera (StationConfig) */}
            <section className="bg-zinc-950/80 border border-zinc-800 rounded-2xl p-6 space-y-4 animate-fade-in">
              <div>
                <h2 className="text-xl font-serif text-chinese-gold tracking-wide">Atmosfera das Estações</h2>
                <p className="text-zinc-500 text-xs mt-1">
                  Personalize os planos de fundo (imagens/vídeos), áudios ambientes e títulos de cada estação da trilha.
                </p>
              </div>

              {/* Seletores de Estação */}
              <div className="space-y-2">
                <label className="text-xs text-zinc-400 font-mono uppercase tracking-wider">Selecione a Estação:</label>
                <div className="flex gap-2">
                  {[1, 2, 3, 4, 5].map((s) => (
                    <button
                      key={s}
                      type="button"
                      onClick={() => handleStationSelect(s)}
                      className={`flex-1 py-2 rounded-lg font-bold text-xs transition-all border ${
                        editingStationId === s
                          ? 'bg-chinese-gold border-chinese-gold text-black shadow-md'
                          : 'bg-zinc-900 border-zinc-800 text-zinc-300 hover:border-chinese-gold/50'
                      }`}
                    >
                      E{s}
                    </button>
                  ))}
                </div>
              </div>

              <form onSubmit={handleSaveStationConfig} className="space-y-4">
                <div className="space-y-1">
                  <label className="text-xs text-zinc-400 font-mono uppercase tracking-wider block">Título da Estação</label>
                  <input
                    type="text"
                    required
                    value={stationTitle}
                    onChange={(e) => setStationTitle(e.target.value)}
                    placeholder="Título customizado"
                    className="w-full bg-zinc-900 border border-zinc-800 rounded-xl py-2 px-3 text-white text-xs focus:outline-none focus:border-chinese-gold transition"
                  />
                </div>

                <div className="space-y-1">
                  <label className="text-xs text-zinc-400 font-mono uppercase tracking-wider block">Subtítulo / Descrição da Estação</label>
                  <textarea
                    rows={2}
                    required
                    value={stationSubtitle}
                    onChange={(e) => setStationSubtitle(e.target.value)}
                    placeholder="Descrição da estação"
                    className="w-full bg-zinc-900 border border-zinc-800 rounded-xl py-2 px-3 text-white text-xs focus:outline-none focus:border-chinese-gold transition resize-none"
                  />
                </div>

                <div className="space-y-1">
                  <label className="text-xs text-zinc-400 font-mono uppercase tracking-wider block">URL da Imagem de Fundo</label>
                  <input
                    type="text"
                    value={stationBgImage}
                    onChange={(e) => setStationBgImage(e.target.value)}
                    placeholder="https://exemplo.com/imagem.jpg"
                    className="w-full bg-zinc-900 border border-zinc-800 rounded-xl py-2 px-3 text-white text-xs focus:outline-none focus:border-chinese-gold transition"
                  />
                </div>

                <div className="space-y-1">
                  <label className="text-xs text-zinc-400 font-mono uppercase tracking-wider block">URL do Vídeo de Fundo (YouTube/mp4)</label>
                  <input
                    type="text"
                    value={stationBgVideo}
                    onChange={(e) => setStationBgVideo(e.target.value)}
                    placeholder="https://youtube.com/watch?v=..."
                    className="w-full bg-zinc-900 border border-zinc-800 rounded-xl py-2 px-3 text-white text-xs focus:outline-none focus:border-chinese-gold transition"
                  />
                </div>

                <div className="space-y-1">
                  <label className="text-xs text-zinc-400 font-mono uppercase tracking-wider block">URL da Melodia de Fundo (MP3)</label>
                  <input
                    type="text"
                    value={stationMelody}
                    onChange={(e) => setStationMelody(e.target.value)}
                    placeholder="https://exemplo.com/musica.mp3"
                    className="w-full bg-zinc-900 border border-zinc-800 rounded-xl py-2 px-3 text-white text-xs focus:outline-none focus:border-chinese-gold transition"
                  />
                </div>

                {editingStationId === 3 && (
                  <>
                    <div className="space-y-1">
                      <label className="text-xs text-zinc-400 font-mono uppercase tracking-wider block">Vídeo Real do Instrumento A (Pipa)</label>
                      <input
                        type="url"
                        value={stationExtraMedia1}
                        onChange={(e) => setStationExtraMedia1(e.target.value)}
                        placeholder="Ex: https://youtube.com/watch?v=..."
                        className="w-full bg-zinc-900 border border-zinc-800 rounded-xl py-2 px-3 text-white text-xs focus:outline-none focus:border-chinese-gold transition"
                      />
                    </div>
                    <div className="space-y-1">
                      <label className="text-xs text-zinc-400 font-mono uppercase tracking-wider block">Vídeo Real do Instrumento B (Erhu)</label>
                      <input
                        type="url"
                        value={stationExtraMedia2}
                        onChange={(e) => setStationExtraMedia2(e.target.value)}
                        placeholder="Ex: https://youtube.com/watch?v=..."
                        className="w-full bg-zinc-900 border border-zinc-800 rounded-xl py-2 px-3 text-white text-xs focus:outline-none focus:border-chinese-gold transition"
                      />
                    </div>
                  </>
                )}

                <button
                  type="submit"
                  className="w-full bg-gradient-to-r from-chinese-gold to-yellow-600 hover:from-yellow-500 hover:to-chinese-gold text-black py-2 rounded-xl font-bold flex items-center justify-center gap-2 active:scale-95 shadow-md transition text-xs"
                >
                  {stationSaveSuccess ? <Check className="w-4 h-4" /> : <Save className="w-4 h-4" />}
                  <span>{stationSaveSuccess ? 'Atmosfera Salva!' : 'Salvar Configurações de Atmosfera'}</span>
                </button>
              </form>
            </section>
          </div>

            {/* Listagem de Alunos e Respostas (7 Colunas) */}
            <section className="lg:col-span-7 bg-zinc-950/80 border border-zinc-800 rounded-2xl p-6 space-y-6">
              <div>
                <h2 className="text-xl font-serif text-zinc-200 tracking-wide">Acompanhamento dos Alunos</h2>
                <p className="text-zinc-500 text-xs mt-1">
                  Veja em tempo real o andamento, pontuações e reviews fornecidos pelos estudantes da feira.
                </p>
              </div>

              {/* Tabela de Estudantes */}
              <div className="overflow-x-auto border border-zinc-900 rounded-xl">
                <table className="w-full text-left border-collapse text-sm">
                  <thead>
                    <tr className="bg-zinc-900 border-b border-zinc-800 text-zinc-400 text-xs uppercase tracking-wider font-mono">
                      <th className="py-4 px-4 font-semibold">Nome / E-mail</th>
                      <th className="py-4 px-4 font-semibold text-center">Nota (Quiz)</th>
                      <th className="py-4 px-4 font-semibold text-center">Pontos XP</th>
                      <th className="py-4 px-4 font-semibold text-center">Estrelas</th>
                      <th className="py-4 px-4 font-semibold text-center">Medalhas</th>
                      <th className="py-4 px-4 font-semibold">Feedback Escrito</th>
                      <th className="py-4 px-4 font-semibold text-center">Ações</th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-zinc-900">
                    {students.length === 0 ? (
                      <tr>
                        <td colSpan={7} className="text-center py-8 text-zinc-600 font-mono">
                          Nenhum aluno realizou a trilha ainda.
                        </td>
                      </tr>
                    ) : (
                      students.map((student) => (
                        <tr key={student.uid} className="hover:bg-zinc-900/30 transition-colors">
                          <td className="py-4 px-4 space-y-0.5">
                            <div className="font-semibold text-zinc-200">{student.name}</div>
                            <div className="text-xs text-zinc-500 font-mono">{student.email}</div>
                          </td>
                          <td className="py-4 px-4 text-center">
                            <span className={`inline-block px-2.5 py-0.5 rounded-full text-xs font-mono font-bold ${
                              student.score >= 4 
                                ? 'bg-green-950 text-green-400' 
                                : student.score >= 2 
                                  ? 'bg-yellow-950 text-yellow-500' 
                                  : 'bg-red-950 text-red-400'
                            }`}>
                              {student.score} / 5
                            </span>
                          </td>
                          <td className="py-4 px-4 text-center font-mono text-xs font-bold text-chinese-gold">
                            {student.xp} XP
                          </td>
                          <td className="py-4 px-4 text-center">
                            <div className="flex items-center justify-center gap-0.5 text-chinese-gold">
                              {student.stars > 0 ? (
                                <>
                                  <Star className="w-4 h-4 fill-chinese-gold" />
                                  <span className="font-bold text-xs font-mono">{student.stars}</span>
                                </>
                              ) : (
                                <span className="text-zinc-600 text-xs font-mono">-</span>
                              )}
                            </div>
                          </td>
                          <td className="py-4 px-4 text-center text-lg select-none">
                            {getBadgeEmojis(student.badges) || <span className="text-zinc-700 text-xs font-mono">-</span>}
                          </td>
                          <td className="py-4 px-4 max-w-xs text-zinc-400 text-xs truncate italic" title={student.feedbackText}>
                            {student.feedbackText || <span className="text-zinc-700 not-italic">Sem comentários</span>}
                          </td>
                          <td className="py-4 px-4 text-center">
                            <button
                              type="button"
                              onClick={() => handleDeleteStudent(student.uid, student.name)}
                              className="p-2 bg-zinc-900/60 hover:bg-red-950 text-zinc-500 hover:text-red-400 border border-zinc-800 hover:border-red-900/40 rounded-xl transition active:scale-95 inline-flex items-center justify-center"
                              title="Excluir Aluno"
                            >
                              <Trash2 className="w-3.5 h-3.5" />
                            </button>
                          </td>
                        </tr>
                      ))
                    )}
                  </tbody>
                </table>
              </div>
            </section>
          </div>
        </main>
      )}
    </div>
  );
}
