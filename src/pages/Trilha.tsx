import { useState, useEffect, useRef } from 'react';
import { useAuth } from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';
import { 
  getQuestions, 
  saveStudentProgress, 
  getStudentsProgress, 
  getRewardVideos, 
  getStationConfigs,
  QuestionCard, 
  StudentProgress, 
  StationConfig 
} from '../lib/db';
import { audio } from '../lib/audio';
import { 
  BookOpen, 
  Star, 
  Award, 
  Check, 
  X, 
  ChevronRight, 
  LogOut, 
  MessageSquare, 
  Lock, 
  Trophy, 
  Volume2, 
  VolumeX, 
  HelpCircle, 
  Compass, 
  Flame, 
  Music, 
  Tv, 
  ShieldAlert, 
  RefreshCw
} from 'lucide-react';

const proverbs = [
  "A jornada de mil milhas começa com um único passo. A sabedoria de amanhã se constrói no esforço e respeito de hoje. — Lao Tzu",
  "O homem que move montanhas começa carregando pequenas pedras. Seja paciente com seu próprio crescimento. — Provérbio Chinês",
  "A água macia fura a pedra dura. A persistência silenciosa vence os maiores desafios. — Provérbio Chinês",
  "O sábio não se exibe, por isso brilha; não se glorifica, por isso é respeitado; não se orgulha, por isso vence. — Lao Tzu",
  "Não tenha medo de crescer lentamente, tenha medo apenas de ficar parado. — Provérbio Chinês",
  "Aquele que vence a si mesmo é o maior guerreiro. O autoconhecimento é a armadura invisível da alma. — Lao Tzu",
  "O vento não pode desestabilizar uma montanha de pedra. O respeito próprio protege contra a opinião alheia. — Provérbio Chinês",
  "A sabedoria ancestral é como uma lanterna: ela ilumina o caminho, mas quem deve dar o passo é você. — Provérbio Chinês",
  "A joia não pode ser polida sem fricção, nem o ser humano aperfeiçoado sem provações. — Provérbio Chinês",
  "Aprender sem refletir é vão; refletir sem aprender é perigoso. Ouça com respeito, decida com sabedoria. — Confúcio",
  "O mestre abre a porta de ouro, mas o caminho do heroísmo deve ser percorrido pelos pés do próprio aprendiz. — Provérbio Chinês",
  "A paz interior é o verdadeiro trono do imperador. Quem domina sua própria mente domina o mundo. — Lao Tzu",
  "O bambu que se curva ao vento é mais forte do que o carvalho que racha na tempestade. Adapte-se e vença. — Provérbio Chinês",
  "A árvore mais forte cresce contra o vento. A superação das dificuldades esculpe o caráter do herói. — Provérbio Chinês",
  "A sabedoria é como a água: ela flui para os lugares mais baixos com humildade, mas nutre a vida de todos. — Lao Tzu",
  "Aquele que pergunta é tolo por cinco minutos; aquele que se cala permanece no erro para sempre. Busque a verdade. — Provérbio Chinês",
  "A honra é como o espelho de jade: uma vez trincada pela desonestidade, nunca recupera o brilho original. — Provérbio Chinês",
  "O sábio aprende com o erro dos outros; o comum aprende com os próprios erros; o insensato nunca aprende. — Provérbio Chinês",
  "O verdadeiro heroísmo está em estender a mão para erguer quem caiu, unindo a força do punho à paz da mente. — Provérbio Chinês",
  "Um livro é como um jardim carregado no bolso. O conhecimento é a única riqueza que ninguém pode roubar. — Provérbio Chinês",
  "Se queres colher frutos para a vida inteira, cultive o caráter e honre a sabedoria dos seus professores. — Provérbio Chinês",
  "A disciplina é a ponte entre os seus sonhos e a sua vitória. Sem foco, o arqueiro erra o alvo mais fácil. — Provérbio Chinês",
  "A palavra dita com respeito é como uma flor de lótus: perfuma a vida de quem fala e de quem escuta. — Provérbio Chinês",
  "Aquele que caminha nos ombros de seus professores enxerga horizontes que a ignorância jamais alcançará. — Provérbio Chinês",
  "O rio atinto seus objetivos porque aprendeu a contornar os obstáculos. Seja persistente como a correnteza. — Provérbio Chinês",
  "A paciência na hora da raiva evita cem dias de arrependimento. Respire e encontre a paz do guerreiro. — Provérbio Chinês",
  "O verdadeiro mestre não ensina o que ver, mas para onde olhar. Honre quem guia seus passos na sabedoria. — Provérbio Chinês",
  "Seja como o lótus, que nasce na lama mas floresce pura sobre a água. Mantenha sua essência intacta. — Provérbio Chinês",
  "A elevação do ser começa no respeito aos mais velhos, na honra aos mestres e na busca incessante pela verdade. — Provérbio Chinês",
  "O guerreiro da sabedoria não busca o aplauso da multidão, mas a paz de estar no caminho certo da retidão. — Provérbio Chinês"
];

// Definição das Medalhas
interface Badge {
  id: string;
  name: string;
  icon: string;
  desc: string;
}

const BADGES: Badge[] = [
  { id: 'badge_1', name: 'Explorador', icon: '🐉', desc: 'Completou o Cap. 1: Jardim do Chá' },
  { id: 'badge_2', name: 'Zodíaco', icon: '🏮', desc: 'Completou o Cap. 2: As Lanternas' },
  { id: 'badge_3', name: 'Ouvido de Ouro', icon: '🎵', desc: 'Completou o Cap. 3: Templo do Erhu' },
  { id: 'badge_4', name: 'Dançarino', icon: '🦁', desc: 'Completou o Cap. 4: A Dança do Dragão' },
  { id: 'badge_5', name: 'Wushu', icon: '⚔️', desc: 'Completou o Cap. 5: Pastel e Wushu' },
  { id: 'badge_final', name: 'Lendário', icon: '👑', desc: 'Concluiu a Trilha e deixou avaliação' }
];

export default function Trilha() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const [questions, setQuestions] = useState<QuestionCard[]>([]);
  const [stationConfigs, setStationConfigs] = useState<StationConfig[]>([]);
  const [leaderboard, setLeaderboard] = useState<StudentProgress[]>([]);
  const [xp, setXp] = useState(0);
  const [earnedBadges, setEarnedBadges] = useState<string[]>([]);
  
  // Rastreamento das Estações concluídas
  const [completedStations, setCompletedStations] = useState<number[]>([]);
  const [activeStation, setActiveStation] = useState<number | null>(null);
  
  // State da Trilha Sonora
  const [isMuted, setIsMuted] = useState(true);

  // Fluxo de Etapas na Estação: 'narrative' | 'game' | 'quiz'
  const [stationStep, setStationStep] = useState<'narrative' | 'game' | 'quiz'>('narrative');

  // State das Estações Específicas
  const [selectedOption, setSelectedOption] = useState<string | null>(null);
  const [score, setScore] = useState(0);
  const [showVideoModal, setShowVideoModal] = useState(false);
  const [currentVideoUrl, setCurrentVideoUrl] = useState('');
  
  // Gerador de Cartas Míticas
  const [showRewardModal, setShowRewardModal] = useState(false);
  const [capturedImage, setCapturedImage] = useState<string | null>(null);
  const [selectedCardBg, setSelectedCardBg] = useState<string>('dragoes_fenix');
  const [selectedCardProverb, setSelectedCardProverb] = useState<string>('');
  const [generatedCardUrl, setGeneratedCardUrl] = useState<string | null>(null);
  const [generationAttempts, setGenerationAttempts] = useState(0);
  const [isCameraActive, setIsCameraActive] = useState(false);
  const videoRef = useRef<HTMLVideoElement | null>(null);

  // Ajustes manuais de fusão de rosto (Y-offset e zoom)
  const [faceYOffset, setFaceYOffset] = useState<number>(0);
  const [faceZoom, setFaceZoom] = useState<number>(1.0);

  // Re-gera a carta automaticamente quando o usuário altera o plano de fundo, o provérbio ou os ajustes de face
  useEffect(() => {
    if (capturedImage) {
      generateCard(capturedImage, selectedCardBg, selectedCardProverb, faceYOffset, faceZoom);
    }
  }, [selectedCardBg, selectedCardProverb, faceYOffset, faceZoom, capturedImage]);

  // Chinese Master Speech / Mei-Ling state
  const [masterSpeech, setMasterSpeech] = useState('');
  const [activeProverb, setActiveProverb] = useState('');
  
  // Rating & Completion states
  const [stars, setStars] = useState(0);
  const [hoveredStars, setHoveredStars] = useState(0);
  const [feedbackText, setFeedbackText] = useState('');
  const [isSaved, setIsSaved] = useState(false);

  // Animação do Dragão Imperial
  const [showDragon, setShowDragon] = useState(false);
  const triggerDragon = () => {
    setShowDragon(true);
    setTimeout(() => setShowDragon(false), 3200);
  };

  // -------------------------------------------------------------
  // GAME STATES PARA OS DESAFIOS INTERATIVOS
  // -------------------------------------------------------------
  
  // Estação 1: Chá
  const [teaSequence, setTeaSequence] = useState<number[]>([]);
  const [teaSuccess, setTeaSuccess] = useState(false);

  // Estação 2: Zodíaco
  const [birthYear, setBirthYear] = useState<string>('');
  const [userZodiac, setUserZodiac] = useState<{ sign: string; desc: string } | null>(null);
  const [starsConnected, setStarsConnected] = useState<number[]>([]);
  const [starsSuccess, setStarsSuccess] = useState(false);

  // Estação 3: Ouvido
  const [earGameAnswer, setEarGameAnswer] = useState<string | null>(null);
  const [earGameFeedback, setEarGameFeedback] = useState('');

  // Estação 4: Ritmo do Tambor
  const [rhythmHits, setRhythmHits] = useState(0);
  const [circleScale, setCircleScale] = useState(1.0);
  const [rhythmMsg, setRhythmMsg] = useState('Aperte Bater Tambor no tempo certo!');
  const rhythmIntervalRef = useRef<number | null>(null);

  // Estação 5: Pastel Fritador
  const [pastelFilling, setPastelFilling] = useState<'queijo' | 'carne' | null>(null);
  const [pastelFryingProgress, setPastelFryingProgress] = useState(0);
  const [pastelFryingState, setPastelFryingState] = useState<'idle' | 'frying' | 'done' | 'burnt' | 'soggy'>('idle');
  const [pastelFryingMsg, setPastelFryingMsg] = useState('Escolha o recheio e frite o pastel!');
  const fryingIntervalRef = useRef<number | null>(null);

  const loadData = async () => {
    try {
      const [list, students, , configs] = await Promise.all([
        getQuestions(), 
        getStudentsProgress(),
        getRewardVideos(),
        getStationConfigs()
      ]);
      setQuestions(list);
      setStationConfigs(configs);
      
      // Ordena o leaderboard por XP decrescente
      const sortedLeaderboard = students
        .filter(s => s.completed)
        .sort((a, b) => b.xp - a.xp || b.score - a.score);
      setLeaderboard(sortedLeaderboard);

      if (user) {
        // Encontra progresso anterior se houver
        const myProgress = students.find(s => s.uid === user.uid);
        if (myProgress) {
          setXp(myProgress.xp || 50);
          setEarnedBadges(myProgress.badges || []);
          setScore(myProgress.score || 0);
          
          // Re-computa as estações concluídas com base nas medalhas
          const completed: number[] = [];
          if (myProgress.badges.includes('badge_1')) completed.push(1);
          if (myProgress.badges.includes('badge_2')) completed.push(2);
          if (myProgress.badges.includes('badge_3')) completed.push(3);
          if (myProgress.badges.includes('badge_4')) completed.push(4);
          if (myProgress.badges.includes('badge_5')) completed.push(5);
          setCompletedStations(completed);
        } else {
          // Inicializa o progresso do novo aluno no banco de dados para capturar nome e e-mail imediatamente
          await saveStudentProgress(user.uid, {
            name: user.displayName || "Aluno Convidado",
            email: user.email || "",
            xp: 50,
            badges: [],
            score: 0,
            completed: false,
            stars: 0
          });
          setXp(50);
        }
      }
    } catch (err) {
      console.error("Erro ao carregar dados da trilha:", err);
    }
  };

  useEffect(() => {
    loadData();
    setIsMuted(audio.getMuteState());
  }, [user]);

  useEffect(() => {
    if (user) {
      setMasterSpeech(
        `Saudações, ${user.displayName}! Eu sou Mei-Ling (梅玲), sua mentora nesta jornada. Este diário guarda mais do que memórias; ele revela o segredo da persistência e superação de quem cruzou o oceano para reconstruir a vida. Venha descobrir o guerreiro pacífico e sábio que existe dentro de você!`
      );
    }
  }, [questions]);

  // Sincroniza o controle da trilha de música de fundo
  const handleToggleMute = () => {
    const muted = audio.toggleMute();
    setIsMuted(muted);
    if (!muted) {
      const isPlayingVideoOrEarChallenge = showVideoModal || (activeStation === 3 && stationStep === 'game');
      if (!isPlayingVideoOrEarChallenge) {
        audio.playBackground();
      }
    } else {
      audio.pauseBackground();
    }
  };

  // Efeito para controlar a reprodução inteligente da trilha sonora de fundo
  // Ela deve pausar caso haja um vídeo em reprodução ou estejamos no jogo de áudio da Estação 3
  useEffect(() => {
    const isPlayingVideoOrEarChallenge = showVideoModal || (activeStation === 3 && stationStep === 'game');
    
    if (isPlayingVideoOrEarChallenge) {
      audio.pauseBackground();
    } else {
      if (!audio.getMuteState()) {
        audio.playBackground();
      }
    }
  }, [activeStation, stationStep, showVideoModal]);

  // Dispara áudio de transição ao entrar em uma estação
  const enterStation = (num: number) => {
    audio.playMysticalGong();
    setActiveStation(num);
    setStationStep('narrative');
    
    // Altera a melodia de fundo conforme a configuração da estação
    const config = stationConfigs.find(c => c.stationId === num);
    audio.setMelodyUrl(config?.melodyUrl || '/audio/traditional_chinese_music.mp3');
    
    // Reseta todos os mini-game states
    setTeaSequence([]);
    setTeaSuccess(false);
    setBirthYear('');
    setUserZodiac(null);
    setStarsConnected([]);
    setStarsSuccess(false);
    setEarGameAnswer(null);
    setEarGameFeedback('');
    setRhythmHits(0);
    setRhythmMsg('Aperte Bater Tambor no tempo certo!');
    setPastelFilling(null);
    setPastelFryingProgress(0);
    setPastelFryingState('idle');
    setPastelFryingMsg('Escolha o recheio e frite o pastel!');
    setSelectedOption(null);

    // Diálogos introdutórios de Mei-Ling para cada estação
    const dialogues = [
      "A paciência é o primeiro escudo do herói. Para preparar o Chá Imperial, Vovô Chen escreve no diário que cada folha tem seu tempo para revelar sua essência e nos dar paz, assim como cada um de nós tem seu próprio tempo de crescimento. Vamos aprender a respeitar o tempo de infusão no Ritual da Paciência?",
      "Olhar para as estrelas nos ensina sobre a nossa própria força interna. O Festival das Lanternas ilumina a noite para afastar o medo e o desânimo. Digite seu ano de nascimento para descobrir a força da sua energia e alinhar as estrelas da sua sorte e autoconhecimento!",
      "A harmonia silenciosa nos dá o foco necessário para vencer as batalhas internas. Em meio ao barulho do mundo digital, o violino chinês Erhu traz um som melancólico que fala com a alma. Teste o seu ouvido atento e tente identificar o timbre sincero do Erhu?",
      "Ninguém vence um grande desafio sozinho. Na vibrante Dança do Dragão, a força da criatura mística depende da união e da sincronia de todos os participantes seguindo a pérola de luz. Pronto para ditar o ritmo dos tambores da perseverança e guiar o dragão com orgulho?",
      "A disciplina é a chave da verdadeira liberdade e do foco. Desde a disciplina milenar do Kung Fu até a preparação cuidadosa e crocante do pastel de feira que nossa família trouxe para o Mato Grosso, cada detalhe exige dedicação. Pronto para focar e fritar o pastel perfeito?"
    ];
    setMasterSpeech(dialogues[num - 1]);
  };

  // Dispara áudio ao voltar para o mapa
  const backToMap = () => {
    audio.playWindChimes();
    setActiveStation(null);
    audio.setMelodyUrl('/audio/traditional_chinese_music.mp3');
    if (fryingIntervalRef.current) {
      clearInterval(fryingIntervalRef.current);
    }
    if (rhythmIntervalRef.current) {
      clearInterval(rhythmIntervalRef.current);
    }
    if (user) {
      setMasterSpeech(`Excelente trabalho, ${user.displayName}! Cada estação concluída fortalece seu espírito e clareia sua mente. Escolha seu próximo desafio no diário de Chen para continuar sua jornada de superação!`);
    }
  };

  // -------------------------------------------------------------
  // LÓGICA MINI-GAMES INTERATIVOS
  // -------------------------------------------------------------

  // 1. Chá
  const handleTeaClick = (id: number) => {
    if (teaSequence.includes(id) || teaSuccess) return;
    audio.playWindChimes();
    const nextSeq = [...teaSequence, id];
    setTeaSequence(nextSeq);

    // Sequência correta:
    // 1: Aquecer água
    // 2: Adicionar folhas de Camellia
    // 3: Aguardar infusão
    // 4: Servir quente
    const expected = [1, 2, 3, 4];
    
    if (nextSeq.length === 4) {
      const isCorrect = nextSeq.every((val, index) => val === expected[index]);
      if (isCorrect) {
        audio.playTeaPour();
        setTeaSuccess(true);
        triggerDragon();
        setMasterSpeech("Que aroma maravilhoso! O chá real está pronto. Agora, responda à pergunta histórica sobre a origem desse cultivo em terras brasileiras.");
        setTimeout(() => {
          setStationStep('quiz');
        }, 2200);
      } else {
        setTeaSequence([]); // Reinicia
        setMasterSpeech("Oops, a ordem de infusão não foi a correta! Vovô Chen dizia que a paciência e a ordem são as chaves. Tente novamente.");
      }
    }
  };

  // 2. Zodíaco
  const calculateZodiac = () => {
    const year = parseInt(birthYear);
    if (isNaN(year) || year < 1900 || year > 2030) {
      alert("Por favor, digite um ano válido entre 1900 e 2030.");
      return;
    }
    audio.playWindChimes();
    const signs = [
      { sign: "Rato", desc: "🐁 Inteligente, adaptável e cheio de recursos." },
      { sign: "Boi", desc: "🐂 Honesto, trabalhador e altamente persistente." },
      { sign: "Tigre", desc: "🐅 Corajoso, impulsivo e um líder por natureza." },
      { sign: "Coelho", desc: "🐇 Gentil, elegante e excelente pacificador." },
      { sign: "Dragão", desc: "🐉 Nobre, poderoso, magnético e cheio de vitalidade." },
      { sign: "Serpente", desc: "🐍 Sábia, enigmática, intuitiva e muito calma." },
      { sign: "Cavalo", desc: "🐎 Ativo, enérgico, independente e apaixonado pela liberdade." },
      { sign: "Cabra", desc: "🐑 Gentil, compassiva, artística e harmoniosa." },
      { sign: "Macaco", desc: "🐒 Curioso, inteligente, inovador e muito brincalhão." },
      { sign: "Galo", desc: "🐓 Observador, corajoso, pontual e orgulhoso." },
      { sign: "Cão", desc: "🐕 Leal, protetor, honesto e sincero." },
      { sign: "Porco", desc: "🐖 Generoso, compassivo, diligente e pacífico." }
    ];
    const index = (year - 1900) % 12;
    setUserZodiac(signs[index]);
    setMasterSpeech(`Interessante! Sob a regência do ${signs[index].sign}, sua essência é: ${signs[index].desc}. Agora, alinhe as 3 estrelas do Dragão conectando os pontos brilhantes!`);
  };

  const connectStar = (starId: number) => {
    if (starsConnected.includes(starId) || starsSuccess) return;
    audio.playWindChimes();
    const nextStars = [...starsConnected, starId];
    setStarsConnected(nextStars);

    if (nextStars.length === 3) {
      setStarsSuccess(true);
      triggerDragon();
      setMasterSpeech("Estrelas conectadas! As lanternas cósmicas estão iluminadas. Responda à pergunta sobre a regência de 2024.");
      setTimeout(() => {
        setStationStep('quiz');
      }, 2000);
    }
  };

  // 3. Ouvido (Música)
  const handleEarSubmit = (answer: string) => {
    if (earGameAnswer) return;
    setEarGameAnswer(answer);

    const q3 = questions.find(q => q.id_grupo === 3);
    const badgeId = 'badge_3';
    let xpGain = 50;

    if (answer === 'erhu') {
      audio.playWindChimes();
      xpGain += 100;
      setXp(prev => prev + xpGain);
      setEarGameFeedback("Sensacional! Você possui um ouvido afinado para as melodias milenares. O Erhu possui duas cordas sintonizadas em intervalos de quinta e seu arco fica entre elas. Assista ao vídeo do grupo.");
      setScore(prev => prev + 1);
      triggerDragon();
    } else {
      setEarGameFeedback("Opa, na verdade o som A era o dedilhado rápido da Pipa. O Erhu (B) é friccionado com arco, gerando esse timbre melancólico parecido com a voz humana. Assista ao vídeo dos alunos para entender mais.");
    }

    const newXP = xp + xpGain;
    const updatedBadges = earnedBadges.includes(badgeId) ? earnedBadges : [...earnedBadges, badgeId];
    setEarnedBadges(updatedBadges);

    const updatedStations = completedStations.includes(3) ? completedStations : [...completedStations, 3];
    setCompletedStations(updatedStations);

    if (user) {
      saveStudentProgress(user.uid, {
        name: user.displayName || "Aluno Convidado",
        email: user.email || "",
        xp: newXP,
        badges: updatedBadges,
        score: score + (answer === 'erhu' ? 1 : 0)
      });
    }

    if (q3) {
      setCurrentVideoUrl(q3.video_feedback_url);
    }
  };

  // 4. Ritmo do Dragão
  const startRhythmGame = () => {
    setStationStep('game');
    setRhythmHits(0);
    
    // Inicia ciclo de animação
    if (rhythmIntervalRef.current) clearInterval(rhythmIntervalRef.current);
    
    let direction = -0.05;
    let scale = 1.0;
    
    rhythmIntervalRef.current = window.setInterval(() => {
      scale += direction;
      if (scale <= 0.4) {
        direction = 0.05;
      } else if (scale >= 1.6) {
        direction = -0.05;
      }
      setCircleScale(scale);
    }, 45);
  };

  const hitDrum = () => {
    audio.playWindChimes();
    // Sucesso quando escala está próxima de 1.0 (entre 0.85 e 1.15)
    if (circleScale >= 0.85 && circleScale <= 1.15) {
      const nextHits = rhythmHits + 1;
      setRhythmHits(nextHits);
      setRhythmMsg(`🔥 Batida Perfeita! Sincronia: ${nextHits}/3`);
      
      if (nextHits >= 3) {
        if (rhythmIntervalRef.current) clearInterval(rhythmIntervalRef.current);
        triggerDragon();
        setRhythmMsg("🎉 Excelente! O Dragão se moveu com perfeita harmonia! Responda à pergunta do Quiz.");
        setTimeout(() => {
          setStationStep('quiz');
        }, 1800);
      }
    } else {
      setRhythmMsg("❌ Fora do tempo! Espere o anel dourado alinhar com o tambor.");
    }
  };

  // 5. Pastel Fritador
  const startFrying = (flavor: 'queijo' | 'carne') => {
    audio.playWindChimes();
    setPastelFilling(flavor);
    setPastelFryingState('frying');
    setPastelFryingProgress(0);
    setPastelFryingMsg("Fritando... retire quando a massa ficar DOURADA!");

    if (fryingIntervalRef.current) clearInterval(fryingIntervalRef.current);

    fryingIntervalRef.current = window.setInterval(() => {
      setPastelFryingProgress(prev => {
        const next = prev + 3;
        audio.playPastelCrunch(); // som de fritura em loop rápido

        if (next >= 100) {
          clearInterval(fryingIntervalRef.current!);
          setPastelFryingState('burnt');
          setPastelFryingMsg("🔥 Ih, queimou! Passou do ponto ideal. Tente novamente!");
          return 100;
        }
        return next;
      });
    }, 150);
  };

  const pullPastel = () => {
    if (fryingIntervalRef.current) clearInterval(fryingIntervalRef.current);
    audio.playWindChimes();
    
    // Perfeito entre 60 e 85%
    if (pastelFryingProgress >= 60 && pastelFryingProgress <= 85) {
      setPastelFryingState('done');
      triggerDragon();
      setPastelFryingMsg("🥟 Crocante e Dourado! Ficou uma delícia! Responda à pergunta do pastel de feira.");
      setTimeout(() => {
        setStationStep('quiz');
      }, 2000);
    } else if (pastelFryingProgress < 60) {
      setPastelFryingState('soggy');
      setPastelFryingMsg("💧 Ih, ficou encharcado! Muito pouco tempo no óleo. Tente novamente.");
    } else {
      setPastelFryingState('burnt');
      setPastelFryingMsg("🔥 Ih, queimou! Passou do ponto ideal. Tente novamente!");
    }
  };

  // -------------------------------------------------------------
  // LÓGICA DO QUIZ GERAL
  // -------------------------------------------------------------
  const handleQuizAnswer = (option: string, question: QuestionCard, stationNum: number) => {
    if (selectedOption) return;
    setSelectedOption(option);
    
    const correct = option === question.resposta_correta;
    let xpGain = 50; // XP Base
    const badgeId = `badge_${stationNum}`;

    if (correct) {
      setScore(prev => prev + 1);
      xpGain += 100; // Acerto
      setMasterSpeech("Incrível! Sua resposta está correta. Veja o trabalho que nossos alunos produziram sobre esse tema!");
      triggerDragon();
    } else {
      setMasterSpeech("Puxa, essa não era a resposta certa. Mas não desanime, o aprendizado é feito de tentativas! Assista ao vídeo de feedback do grupo.");
    }

    const newXP = xp + xpGain;
    setXp(newXP);

    const updatedBadges = earnedBadges.includes(badgeId) ? earnedBadges : [...earnedBadges, badgeId];
    setEarnedBadges(updatedBadges);

    const updatedStations = completedStations.includes(stationNum) ? completedStations : [...completedStations, stationNum];
    setCompletedStations(updatedStations);

    if (user) {
      saveStudentProgress(user.uid, {
        name: user.displayName || "Aluno Convidado",
        email: user.email || "",
        score: score + (correct ? 1 : 0),
        xp: newXP,
        badges: updatedBadges,
        completed: false
      });
    }

    setCurrentVideoUrl(question.video_feedback_url);
    setShowVideoModal(true);
  };

  // Finalização da Avaliação
  const handleFinishedEvaluation = async () => {
    if (!user) return;
    const badgeId = 'badge_final';
    const finalXP = xp + 100; // XP por feedback
    const finalBadges = earnedBadges.includes(badgeId) ? earnedBadges : [...earnedBadges, badgeId];
    
    try {
      await saveStudentProgress(user.uid, {
        name: user.displayName || "Aluno Convidado",
        email: user.email || "",
        xp: finalXP,
        badges: finalBadges,
        stars: stars,
        feedbackText: feedbackText,
        completed: true
      });
      
      setXp(finalXP);
      setEarnedBadges(finalBadges);
      setIsSaved(true);
      triggerDragon();
      setMasterSpeech(`Xie Xie (Obrigado), ${user.displayName}! Sua jornada de superação e autoconhecimento foi concluída. Cada desafio superado fortaleceu sua determinação para vencer as batalhas reais da vida. Você é a nossa Lenda Imperial!`);
      
      // Atualiza o ranking
      const students = await getStudentsProgress();
      const sortedLeaderboard = students
        .filter(s => s.completed)
        .sort((a, b) => b.xp - a.xp || b.score - a.score);
      setLeaderboard(sortedLeaderboard);

      const idx = Math.floor(Math.random() * proverbs.length);
      setActiveProverb(proverbs[idx]);

      // Abre o modal do Gerador de Cartas Míticas
      setShowRewardModal(true);
    } catch (e) {
      console.error(e);
    }
  };

  // Métodos do Gerador de Cartas Míticas
  const generateProverbWithGemini = async (studentName: string, zodiacSign: string): Promise<string | null> => {
    const apiKey = import.meta.env.VITE_GEMINI_API_KEY;
    if (!apiKey) return null;

    try {
      const prompt = `Crie uma mensagem poética curta e inspiradora (máximo 140 caracteres) para a Carta Mítica do estudante '${studentName}' da Feira das Nações de 2026. Ele/ela é do 7º ano e seu signo chinês é '${zodiacSign}'.
A mensagem deve unir sabedoria chinesa tradicional, disciplina e esforço nos estudos como um caminho de honra e superação pessoal.
Escreva a frase de forma direta e inspiradora. Não adicione nenhuma introdução, aspas desnecessárias ou texto extra. Retorne apenas a reflexão.`;

      const response = await fetch(`https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=${apiKey}`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          contents: [{
            parts: [{ text: prompt }]
          }]
        })
      });

      if (!response.ok) {
        throw new Error('Erro na resposta do Gemini API');
      }

      const data = await response.json();
      const text = data.candidates?.[0]?.content?.parts?.[0]?.text;
      if (text) {
        return text.trim().replace(/^"(.*)"$/, '$1');
      }
    } catch (error) {
      console.error("Erro ao gerar provérbio com Gemini:", error);
    }
    return null;
  };

  const startCamera = async () => {
    try {
      setIsCameraActive(true);
      setGeneratedCardUrl(null);
      const stream = await navigator.mediaDevices.getUserMedia({ 
        video: { facingMode: 'user', width: 480, height: 480 } 
      });
      if (videoRef.current) {
        videoRef.current.srcObject = stream;
      }
    } catch (err) {
      console.error("Erro ao acessar câmera:", err);
      alert("Não foi possível acessar a câmera do dispositivo. Utilize a opção de carregar uma foto do seu dispositivo!");
      setIsCameraActive(false);
    }
  };

  const stopCamera = () => {
    if (videoRef.current && videoRef.current.srcObject) {
      const stream = videoRef.current.srcObject as MediaStream;
      stream.getTracks().forEach(track => track.stop());
      videoRef.current.srcObject = null;
    }
    setIsCameraActive(false);
  };

  const capturePhoto = async () => {
    if (videoRef.current) {
      const canvas = document.createElement('canvas');
      canvas.width = 480;
      canvas.height = 480;
      const ctx = canvas.getContext('2d');
      if (ctx) {
        const video = videoRef.current;
        const videoWidth = video.videoWidth || 480;
        const videoHeight = video.videoHeight || 480;
        const size = Math.min(videoWidth, videoHeight);
        const sx = (videoWidth - size) / 2;
        const sy = (videoHeight - size) / 2;
        ctx.drawImage(video, sx, sy, size, size, 0, 0, 480, 480);
        const dataUrl = canvas.toDataURL('image/png');
        
        if (generationAttempts >= 3) {
          alert("Você já atingiu o limite de 3 tentativas de geração de Carta Mítica!");
          return;
        }
        setGenerationAttempts(prev => prev + 1);
        setCapturedImage(dataUrl);
        stopCamera();

        let proverb = proverbs[Math.floor(Math.random() * proverbs.length)];
        const apiKey = import.meta.env.VITE_GEMINI_API_KEY;
        
        if (apiKey) {
          // Mostra feedback visual de carregamento imediato na carta
          setSelectedCardProverb("Consultando o Oráculo Imperial...");
          generateCard(dataUrl, selectedCardBg, "Consultando o Oráculo Imperial...", faceYOffset, faceZoom);
          
          const dynamicProverb = await generateProverbWithGemini(
            user?.displayName || 'Eterno Aprendiz',
            userZodiac?.sign || 'Dragão'
          );
          if (dynamicProverb) {
            proverb = dynamicProverb;
          }
        }
        
        setSelectedCardProverb(proverb);
        generateCard(dataUrl, selectedCardBg, proverb, faceYOffset, faceZoom);
      }
    }
  };

  const handleImageUpload = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) {
      if (generationAttempts >= 3) {
        alert("Você já atingiu o limite de 3 tentativas de geração de Carta Mítica!");
        return;
      }
      setGenerationAttempts(prev => prev + 1);

      const reader = new FileReader();
      reader.onload = async (event) => {
        const dataUrl = event.target?.result as string;
        setCapturedImage(dataUrl);

        let proverb = proverbs[Math.floor(Math.random() * proverbs.length)];
        const apiKey = import.meta.env.VITE_GEMINI_API_KEY;
        
        if (apiKey) {
          // Mostra feedback visual de carregamento imediato na carta
          setSelectedCardProverb("Consultando o Oráculo Imperial...");
          generateCard(dataUrl, selectedCardBg, "Consultando o Oráculo Imperial...", faceYOffset, faceZoom);
          
          const dynamicProverb = await generateProverbWithGemini(
            user?.displayName || 'Eterno Aprendiz',
            userZodiac?.sign || 'Dragão'
          );
          if (dynamicProverb) {
            proverb = dynamicProverb;
          }
        }
        
        setSelectedCardProverb(proverb);
        generateCard(dataUrl, selectedCardBg, proverb, faceYOffset, faceZoom);
      };
      reader.readAsDataURL(file);
    }
  };

  const generateWithoutPhoto = async () => {
    if (generationAttempts >= 3) {
      alert("Você já atingiu o limite de 3 tentativas de geração de Carta Mítica!");
      return;
    }
    setGenerationAttempts(prev => prev + 1);
    setCapturedImage("none");

    let proverb = proverbs[Math.floor(Math.random() * proverbs.length)];
    const apiKey = import.meta.env.VITE_GEMINI_API_KEY;
    
    if (apiKey) {
      setSelectedCardProverb("Consultando o Oráculo Imperial...");
      generateCard("none", selectedCardBg, "Consultando o Oráculo Imperial...", faceYOffset, faceZoom);
      
      const dynamicProverb = await generateProverbWithGemini(
        user?.displayName || 'Eterno Aprendiz',
        userZodiac?.sign || 'Dragão'
      );
      if (dynamicProverb) {
        proverb = dynamicProverb;
      }
    }
    
    setSelectedCardProverb(proverb);
    generateCard("none", selectedCardBg, proverb, faceYOffset, faceZoom);
  };

  const generateCard = (
    photoUrl: string, 
    bgName: string = selectedCardBg, 
    proverbText: string = selectedCardProverb,
    yOffset: number = faceYOffset,
    zoom: number = faceZoom
  ) => {
    const canvas = document.createElement('canvas');
    canvas.width = 600;
    canvas.height = 850;
    const ctx = canvas.getContext('2d');
    if (!ctx) return;

    const hasPhoto = photoUrl && photoUrl !== "none";

    // Pré-carregamento seguro de todos os assets visuais para o Canvas
    const bgImg = new Image();
    bgImg.src = `/images/${bgName}.png`;

    const logoCov = new Image();
    logoCov.src = `/images/LOGO_COV.png`;

    const logoCivico = new Image();
    logoCivico.src = `/images/LOGO_CIVICO_COV.png`;

    const logoSeduc = new Image();
    logoSeduc.src = `/images/SOMOSTODOSEDU.png`;

    const studentImg = new Image();
    if (hasPhoto) {
      studentImg.src = photoUrl;
    }

    let loadedCount = 0;
    const totalImages = hasPhoto ? 5 : 4;

    const onImageLoaded = () => {
      loadedCount++;
      if (loadedCount === totalImages) {
        drawAll();
      }
    };

    // Caso ocorra algum erro ao carregar logos locais, prossegue usando fallbacks para não travar a geração
    const onImageError = (e: any) => {
      console.warn("Falha ao carregar imagem no Canvas, prosseguindo com fallback:", e.target?.src);
      onImageLoaded();
    };

    bgImg.onload = onImageLoaded;
    logoCov.onload = onImageLoaded;
    logoCivico.onload = onImageLoaded;
    logoSeduc.onload = onImageLoaded;
    if (hasPhoto) {
      studentImg.onload = onImageLoaded;
    }

    bgImg.onerror = onImageError;
    logoCov.onerror = onImageError;
    logoCivico.onerror = onImageError;
    logoSeduc.onerror = onImageError;
    if (hasPhoto) {
      studentImg.onerror = onImageError;
    }

    const drawAll = () => {
      // 1. Fundo com Imagem selecionada pelo usuário (Efeito Cover)
      const scale = Math.max(600 / bgImg.width, 850 / bgImg.height);
      const x = (600 - bgImg.width * scale) / 2;
      const y = (850 - bgImg.height * scale) / 2;
      ctx.drawImage(bgImg, x, y, bgImg.width * scale, bgImg.height * scale);

      // 2. Barra Superior (Crimson Imperial para máxima legibilidade institucional)
      ctx.fillStyle = '#4a0707'; // Vermelho Escuro Imperial / Burgundy
      ctx.fillRect(0, 0, 600, 135);
      
      // Linha dourada inferior da barra superior
      ctx.strokeStyle = '#fbbf24';
      ctx.lineWidth = 3;
      ctx.beginPath();
      ctx.moveTo(0, 135);
      ctx.lineTo(600, 135);
      ctx.stroke();

      // Desenhar Logotipos da Escola
      if (logoCov.complete && logoCov.naturalWidth > 0) {
        ctx.drawImage(logoCov, 25, 20, 90, 90);
      }
      if (logoCivico.complete && logoCivico.naturalWidth > 0) {
        ctx.drawImage(logoCivico, 485, 20, 90, 90);
      }

      // Textos da Barra Superior (Brancos e Dourados de Alta Legibilidade)
      ctx.shadowColor = 'rgba(0, 0, 0, 0.9)';
      ctx.shadowBlur = 4;
      ctx.shadowOffsetX = 1;
      ctx.shadowOffsetY = 1;
      ctx.textAlign = 'center';

      ctx.fillStyle = '#fbbf24';
      ctx.font = 'bold 14px Georgia, serif';
      ctx.fillText("EE CM Cremilda de Oliveira - 2026", 300, 48);

      ctx.fillStyle = '#fef08a'; // yellow-200
      ctx.font = 'bold 21px Georgia, serif';
      ctx.fillText("CARTA MÍTICA DA SABEDORIA", 300, 80);

      ctx.fillStyle = '#f87171'; // Vermelho Suave
      ctx.font = 'bold 9px Courier, monospace';
      ctx.fillText("FEIRA DAS NAÇÕES • CULTURA CHINESA NO BRASIL", 300, 108);

      // 3. Desenho da foto recortada e fundida (Feathered Face Swap) se houver foto
      if (hasPhoto) {
        // Coordenadas ideais de alinhamento da face de acordo com o fundo
        const bgFaceCoords: Record<string, { x: number; y: number; r: number }> = {
          dragoes_fenix: { x: 300, y: 275, r: 90 },
          explosiva: { x: 300, y: 255, r: 85 },
          cultura_cha: { x: 300, y: 275, r: 90 },
          instrumentos: { x: 305, y: 282, r: 80 },
          festival: { x: 300, y: 275, r: 90 },
        };

        const coord = bgFaceCoords[bgName] || { x: 300, y: 275, r: 90 };
        
        const targetX = coord.x;
        const targetY = coord.y + yOffset;
        const targetR = coord.r * zoom;
        const size = Math.round(targetR * 2);

        // Cria um canvas temporário com o tamanho da face para aplicar o degradê radial de suavização (feather)
        const tempCanvas = document.createElement('canvas');
        tempCanvas.width = size;
        tempCanvas.height = size;
        const tempCtx = tempCanvas.getContext('2d');

        if (tempCtx && studentImg.complete && studentImg.naturalWidth > 0) {
          const photoScale = Math.max(size / studentImg.width, size / studentImg.height);
          const photoW = studentImg.width * photoScale;
          const photoH = studentImg.height * photoScale;
          const photoX = (size - photoW) / 2;
          const photoY = (size - photoH) / 2;

          let photoFilter = 'brightness(1.05) contrast(1.05)';
          if (bgName === 'explosiva' || bgName === 'festival') {
            photoFilter += ' sepia(0.2) saturate(1.25) hue-rotate(-5deg)';
          } else if (bgName === 'dragoes_fenix') {
            photoFilter += ' saturate(0.9) hue-rotate(5deg)';
          } else if (bgName === 'cultura_cha' || bgName === 'instrumentos') {
            photoFilter += ' sepia(0.08) saturate(1.1)';
          }
          tempCtx.filter = photoFilter;

          tempCtx.drawImage(studentImg, photoX, photoY, photoW, photoH);
          tempCtx.filter = 'none';

          const maskGradient = tempCtx.createRadialGradient(
            size / 2, size / 2, size * 0.32, // Centro totalmente visível
            size / 2, size / 2, size / 2       // Borda externa transparente
          );
          maskGradient.addColorStop(0, 'rgba(0,0,0,1)');
          maskGradient.addColorStop(0.7, 'rgba(0,0,0,0.85)');
          maskGradient.addColorStop(1, 'rgba(0,0,0,0)');

          tempCtx.globalCompositeOperation = 'destination-in';
          tempCtx.fillStyle = maskGradient;
          tempCtx.beginPath();
          tempCtx.arc(size / 2, size / 2, size / 2, 0, Math.PI * 2);
          tempCtx.fill();
          tempCtx.globalCompositeOperation = 'source-over';

          const drawX = targetX - targetR;
          const drawY = targetY - targetR;
          ctx.drawImage(tempCanvas, drawX, drawY);
        }

        // Desenha o anel sutil apenas se for fundo sem personagem central (medalhão)
        if (bgName !== 'explosiva' && bgName !== 'instrumentos') {
          ctx.strokeStyle = 'rgba(251, 191, 36, 0.45)'; // yellow-400 com 45% opacidade
          ctx.lineWidth = 3;
          ctx.beginPath();
          ctx.arc(targetX, targetY, targetR, 0, Math.PI * 2);
          ctx.stroke();
        }
      }

      // 4. Detalhes do Aluno no Centro (Nome, Horóscopo, XP)
      ctx.shadowColor = 'rgba(0, 0, 0, 0.95)';
      ctx.shadowBlur = 8;
      ctx.shadowOffsetX = 2;
      ctx.shadowOffsetY = 2;
      ctx.textAlign = 'center';

      // Nome do Aluno
      ctx.fillStyle = '#ffffff';
      ctx.font = 'bold 24px Georgia, serif';
      const studentName = user?.displayName || 'Eterno Aprendiz';
      ctx.fillText(studentName, 300, 435);

      // Signo e Info do Horóscopo do Aluno
      ctx.fillStyle = '#fbbf24';
      ctx.font = '14px Georgia, serif';
      const zodiacText = userZodiac 
        ? `Signo Chinês Regente: ${userZodiac.sign} ${userZodiac.sign === 'Dragão' ? '🐉' : '🏮'}` 
        : 'Signo Chinês Regente: Dragão Imperial 🐉';
      ctx.fillText(zodiacText, 300, 465);

      // Pontos XP
      ctx.fillStyle = '#f59e0b';
      ctx.font = 'bold 13px Courier, monospace';
      ctx.fillText(`JORNADA COMPLETA: ${xp} XP ACUMULADOS`, 300, 490);

      // 5. Caixa do Provérbio Estilo Papiro (Fundo Bege Papiro com Letra Marrom Escura)
      ctx.shadowBlur = 0;
      ctx.shadowOffsetX = 0;
      ctx.shadowOffsetY = 0;

      // Fundo do Papiro
      ctx.fillStyle = '#fbf5e2'; // Bege papiro claro tradicional
      ctx.strokeStyle = '#5c2d12'; // Moldura marrom papiro
      ctx.lineWidth = 3;
      ctx.beginPath();
      if (typeof ctx.roundRect === 'function') {
        ctx.roundRect(60, 520, 480, 155, 12);
      } else {
        ctx.rect(60, 520, 480, 155);
      }
      ctx.fill();
      ctx.stroke();

      // Borda interna fina dourada/laranja no papiro
      ctx.strokeStyle = '#d97706';
      ctx.lineWidth = 1;
      ctx.beginPath();
      if (typeof ctx.roundRect === 'function') {
        ctx.roundRect(65, 525, 470, 145, 8);
      } else {
        ctx.rect(65, 525, 470, 145);
      }
      ctx.stroke();

      // Configuração de texto do Provérbio sobre o Papiro (Contraste Perfeito)
      ctx.shadowColor = 'rgba(0, 0, 0, 0.15)';
      ctx.shadowBlur = 2;
      ctx.shadowOffsetX = 1;
      ctx.shadowOffsetY = 1;
      ctx.fillStyle = '#3d2314'; // Letra Marrom Escura
      ctx.font = 'italic bold 16px Georgia, serif';
      ctx.textAlign = 'center';
      
      const splitText = (text: string, maxChars: number = 42): string[] => {
        const words = text.split(' ');
        const lines: string[] = [];
        let currentLine = '';
        
        words.forEach(word => {
          if ((currentLine + word).length > maxChars) {
            lines.push(currentLine.trim());
            currentLine = word + ' ';
          } else {
            currentLine += word + ' ';
          }
        });
        if (currentLine) {
          lines.push(currentLine.trim());
        }
        return lines;
      };

      const proverbLines = splitText(proverbText || "O sábio busca o autoconhecimento como um eterno aprendiz da sabedoria ancestral.");

      proverbLines.forEach((line, idx) => {
        ctx.fillText(line, 300, 558 + (idx * 26));
      });

      // 6. Barra Inferior (Crimson Imperial / Bordas Ouro)
      ctx.shadowBlur = 0;
      ctx.shadowOffsetX = 0;
      ctx.shadowOffsetY = 0;
      
      ctx.fillStyle = '#4a0707'; // Burgundy
      ctx.fillRect(0, 740, 600, 110);

      // Linha dourada superior da barra inferior
      ctx.strokeStyle = '#fbbf24';
      ctx.lineWidth = 3;
      ctx.beginPath();
      ctx.moveTo(0, 740);
      ctx.lineTo(600, 740);
      ctx.stroke();

      // Desenhar Logotipo da SEDUC à esquerda
      if (logoSeduc.complete && logoSeduc.naturalWidth > 0) {
        ctx.drawImage(logoSeduc, 25, 752, 140, 42);
      }

      // Textos da Barra Inferior (Alta legibilidade e contraste)
      ctx.shadowColor = 'rgba(0, 0, 0, 0.9)';
      ctx.shadowBlur = 4;
      ctx.shadowOffsetX = 1;
      ctx.shadowOffsetY = 1;
      ctx.textAlign = 'center';

      ctx.fillStyle = '#fbbf24';
      ctx.font = 'bold 13px Georgia, serif';
      ctx.fillText("7º ANO D • PROF. JOHNNY FERNANDES", 380, 778);
      
      ctx.font = '9px Courier, monospace';
      ctx.fillStyle = '#fef08a';
      ctx.fillText("© Demonstrando a Força da Disciplina e do Conhecimento", 380, 804);

      // 7. Moldura Externa Ouro Geral sobreposta
      ctx.strokeStyle = '#d97706'; // amber-600
      ctx.lineWidth = 6;
      ctx.strokeRect(15, 15, 570, 820);
      
      ctx.strokeStyle = '#fbbf24'; // yellow-400
      ctx.lineWidth = 2;
      ctx.strokeRect(22, 22, 556, 806);

      // Detalhes dos cantos
      const drawCorner = (x: number, y: number, angle: number) => {
        ctx.save();
        ctx.translate(x, y);
        ctx.rotate(angle);
        ctx.strokeStyle = '#fbbf24';
        ctx.lineWidth = 4;
        ctx.beginPath();
        ctx.moveTo(0, 30);
        ctx.lineTo(0, 0);
        ctx.lineTo(30, 0);
        ctx.stroke();
        ctx.restore();
      };
      drawCorner(22, 22, 0);
      drawCorner(578, 22, Math.PI / 2);
      drawCorner(578, 828, Math.PI);
      drawCorner(22, 828, -Math.PI / 2);

      // Converte o canvas resultante para URL de imagem reativa
      const dataUrl = canvas.toDataURL('image/png');
      setGeneratedCardUrl(dataUrl);
    };
  };

  const downloadCard = () => {
    if (generatedCardUrl) {
      const link = document.createElement('a');
      link.download = `Carta_Mitica_${user?.displayName || 'Estudante'}.png`;
      link.href = generatedCardUrl;
      link.click();
    }
  };

  const handleLogout = async () => {
    await logout();
    navigate('/');
  };

  const allCompleted = completedStations.length === 5;
  const currentStationConfig = stationConfigs.find(c => c.stationId === activeStation);

  return (
    <div className="min-h-screen w-full bg-red-950 text-white flex flex-col font-sans relative overflow-x-hidden">
      
      {/* Lanternas Vermelhas Flutuantes de Fundo */}
      <div className="absolute inset-0 pointer-events-none overflow-hidden z-0">
        {Array.from({ length: 15 }).map((_, i) => (
          <span
            key={i}
            className="lantern-particle select-none animate-pulse-slow"
            style={{
              left: `${(i * 6.5) + 3}%`,
              animationDelay: `${i * 1.3}s`,
              animationDuration: `${11 + Math.random() * 7}s`
            }}
          >
            🏮
          </span>
        ))}
      </div>
      
      {/* Background Live Video / Image / Fallback Gradients */}
      {activeStation !== null && currentStationConfig ? (
        <div className="absolute inset-0 w-full h-full -z-10 overflow-hidden">
          {currentStationConfig.backgroundVideoUrl ? (
            <div className="absolute inset-0 w-full h-full pointer-events-none scale-110">
              <iframe
                className="w-full h-full opacity-20 filter grayscale-50"
                src={`${currentStationConfig.backgroundVideoUrl}?autoplay=1&mute=1&loop=1&playlist=${currentStationConfig.backgroundVideoUrl.split('/').pop()}&controls=0`}
                allow="autoplay; encrypted-media"
                frameBorder="0"
              ></iframe>
              <div className="absolute inset-0 bg-black/60"></div>
            </div>
          ) : currentStationConfig.backgroundImageUrl ? (
            <div 
              className="absolute inset-0 bg-cover bg-center opacity-15"
              style={{ backgroundImage: `url(${currentStationConfig.backgroundImageUrl})` }}
            >
              <div className="absolute inset-0 bg-black/40"></div>
            </div>
          ) : (
            <div className="absolute inset-0 bg-gradient-to-b from-zinc-950 via-chinese-dark to-zinc-950 opacity-90"></div>
          )}
        </div>
      ) : (
        <>
          <div className="absolute top-1/4 left-10 w-80 h-80 bg-chinese-red/5 rounded-full blur-3xl animate-pulse-slow -z-10"></div>
          <div className="absolute bottom-1/4 right-10 w-80 h-80 bg-chinese-gold/5 rounded-full blur-3xl animate-pulse-slow -z-10" style={{ animationDelay: '2s' }}></div>
        </>
      )}

      {/* Header */}
      <header className="border-b border-chinese-gold/20 bg-zinc-950/80 backdrop-blur-md sticky top-0 z-30 px-6 py-3 flex flex-col sm:flex-row gap-3 items-center justify-between shadow-md">
        <div className="flex items-center gap-3">
          <BookOpen className="w-6 h-6 text-chinese-gold" />
          <div>
            <h1 className="font-serif tracking-wider text-chinese-gold text-base sm:text-lg">
              Feira das Nações: Experiência Sino-Brasileira
            </h1>
            <p className="text-[10px] text-zinc-500 font-mono tracking-widest">7º ANO D • PROF. JOHNNY FERNANDES • EECOV</p>
          </div>
        </div>

        {/* Painel de XP, Mídias e Medalhas */}
        <div className="flex items-center gap-4">
          
          {/* Mute/Unmute da Trilha de Fundo */}
          <button
            onClick={handleToggleMute}
            className="p-2 bg-zinc-900 hover:bg-zinc-800 text-chinese-gold border border-zinc-800 rounded-xl transition active:scale-95"
            title={isMuted ? "Tocar Música de Fundo" : "Mutar Música"}
          >
            {isMuted ? <VolumeX className="w-4 h-4" /> : <Volume2 className="w-4 h-4 animate-bounce" />}
          </button>

          <div className="bg-zinc-900 border border-zinc-800 rounded-xl px-4 py-1.5 flex items-center gap-2">
            <Trophy className="w-4 h-4 text-chinese-gold" />
            <div>
              <div className="text-[9px] font-mono text-zinc-500 uppercase">Pontos XP</div>
              <div className="text-sm font-bold text-chinese-gold leading-none">{xp} XP</div>
            </div>
          </div>

          <div className="flex items-center gap-1.5 bg-zinc-900 border border-zinc-800 rounded-xl px-3 py-1">
            {BADGES.map(b => {
              const isEarned = earnedBadges.includes(b.id);
              return (
                <span 
                  key={b.id} 
                  title={`${b.name}: ${b.desc}`} 
                  className={`text-xl select-none transition-all duration-300 ${isEarned ? 'scale-100 filter-none filter drop-shadow-[0_0_4px_rgba(255,215,0,0.4)]' : 'scale-90 filter grayscale opacity-25'}`}
                >
                  {b.icon}
                </span>
              );
            })}
          </div>

          <button 
            onClick={handleLogout}
            className="flex items-center gap-1.5 text-zinc-400 hover:text-chinese-red text-xs transition font-semibold"
          >
            <LogOut className="w-3.5 h-3.5" />
            <span>Sair</span>
          </button>
        </div>
      </header>

      {/* Main Layout */}
      <main className="flex-1 max-w-7xl w-full mx-auto p-4 sm:p-6 grid grid-cols-1 lg:grid-cols-12 gap-8 items-start">
        
        {/* Lado Esquerdo: Mei-Ling + Estação Ativa ou Mapa (8 Colunas) */}
        <section className="lg:col-span-8 flex flex-col gap-6">
          
          {/* Mei-Ling - Narradora Principal (Glassmorphism Premium) */}
          <div className="bg-red-950/80 border border-yellow-600/35 chinese-border-double rounded-2xl p-5 backdrop-blur-md shadow-[0_4px_25px_rgba(255,215,0,0.08)] flex gap-5 items-start relative overflow-hidden">
            <div className="absolute top-0 right-0 w-24 h-24 bg-yellow-500/5 rounded-full blur-xl pointer-events-none"></div>
            
            <div className="relative flex-shrink-0">
              <div className="w-16 h-16 bg-gradient-to-br from-red-700 to-red-950 border border-yellow-500 rounded-full flex items-center justify-center overflow-hidden shadow-lg ring-2 ring-yellow-500/30">
                <img 
                  src="/mei_ling_avatar.png" 
                  alt="Mei-Ling" 
                  className="w-full h-full object-cover"
                />
              </div>
              <span className="absolute -bottom-1 -right-1 w-5 h-5 bg-red-600 rounded-full flex items-center justify-center border border-yellow-500 text-[10px] animate-bounce">✨</span>
            </div>
            <div className="space-y-1">
              <h4 className="font-serif text-chinese-gold text-sm tracking-wider font-bold flex items-center gap-2">
                <span>Mei-Ling</span>
                <span className="text-[10px] bg-chinese-red/20 text-chinese-red border border-chinese-red/30 px-1.5 py-0.5 rounded-full font-mono font-normal">Guia da Trilha</span>
              </h4>
              <p className="text-zinc-200 text-sm italic leading-relaxed">
                "{masterSpeech}"
              </p>
            </div>
          </div>

          {activeStation === null && !allCompleted && (
            /* MAPA DE ESTAÇÕES */
            <div className="bg-red-950/80 border border-yellow-600/30 chinese-border rounded-3xl p-6 sm:p-8 shadow-2xl relative z-10">
              <div className="text-center mb-6">
                <h2 className="text-2xl font-serif text-chinese-gold">As 5 Memórias do Diário de Chen</h2>
                <p className="text-zinc-500 text-xs">Acompanhe a trilha na ordem histórica para coletar XP e desvendar conquistas.</p>
              </div>

              {/* Caminho Gráfico / Estações */}
              <div className="flex flex-col gap-6 relative max-w-xl mx-auto py-4">
                {[1, 2, 3, 4, 5].map((num) => {
                  const isCompleted = completedStations.includes(num);
                  const isUnlocked = num === 1 || completedStations.includes(num - 1);
                  
                  const icons = [<Compass/>, <Flame/>, <Music/>, <Tv/>, <HelpCircle/>];
                  const labels = [
                    "Capítulo 1: O Aroma do Chá Real (História & Raízes)",
                    "Capítulo 2: O Brilho das Lanternas (Zodíaco & Astronomia)",
                    "Capítulo 3: O Eco do Erhu (Música & Equilíbrio)",
                    "Capítulo 4: A Dança da Sabedoria (Danças & Celebrações)",
                    "Capítulo 5: A Alquimia da Feira (Pastel & Wushu)"
                  ];

                  return (
                    <div key={num} className="flex items-center gap-4 relative z-10">
                      {num < 5 && (
                        <div className={`absolute left-7 top-14 w-0.5 h-12 -z-10 ${completedStations.includes(num) ? 'bg-yellow-500' : 'bg-red-900/60'}`} />
                      )}

                      <button
                        disabled={!isUnlocked}
                        onClick={() => enterStation(num)}
                        className={`w-14 h-14 rounded-full flex items-center justify-center border-2 transition-all duration-300 ${
                          isCompleted
                            ? 'bg-emerald-950/60 border-emerald-500 text-emerald-450 shadow-[0_0_12px_rgba(16,185,129,0.3)]'
                            : isUnlocked
                              ? 'bg-red-900 border-yellow-500 text-yellow-450 animate-pulse shadow-[0_0_15px_rgba(255,215,0,0.2)] hover:scale-105'
                              : 'bg-red-950/40 border-red-900/60 text-red-900/40 cursor-not-allowed'
                        }`}
                      >
                        {isCompleted ? <Check className="w-6 h-6" /> : !isUnlocked ? <Lock className="w-5 h-5" /> : icons[num - 1]}
                      </button>

                      <div className="flex-1">
                        <div className={`text-sm font-bold tracking-wide ${isUnlocked ? 'text-zinc-200' : 'text-zinc-600'}`}>
                          {labels[num - 1]}
                        </div>
                        <div className="text-[11px] text-zinc-500 font-mono">
                          {isCompleted ? '✓ Concluído (+150 XP)' : isUnlocked ? '⚡ Iniciar Capítulo' : '🔒 Conclua o capítulo anterior'}
                        </div>
                      </div>
                    </div>
                  );
                })}
              </div>
            </div>
          )}

          {activeStation !== null && (
            /* CONTEÚDO DA ESTAÇÃO SELECIONADA */
            <div className={`border border-yellow-600/40 chinese-border rounded-3xl p-6 sm:p-8 shadow-2xl relative animate-fade-in ${
              activeStation === 1 || activeStation === 5 ? 'bg-emerald-995/95' : 'bg-red-995/95'
            }`}>
              
              {/* Botão Voltar para o Mapa */}
              <button 
                onClick={backToMap}
                className="absolute top-6 right-6 text-xs text-zinc-500 hover:text-chinese-gold flex items-center gap-1 font-mono uppercase tracking-wider transition"
              >
                <ChevronRight className="w-4 h-4 rotate-180" />
                <span>Diário</span>
              </button>

              <div className="mb-6">
                <span className="bg-chinese-red/20 text-chinese-red text-xs font-mono font-bold px-3 py-1 rounded-full border border-chinese-red/30">
                  Capítulo {activeStation} • {currentStationConfig?.customTitle || "Atmosfera Chinesa"}
                </span>
                <p className="text-zinc-500 text-xs mt-2 italic font-mono">{currentStationConfig?.customSubtitle}</p>
              </div>

              {/* -------------------------------------------------------------
                  FASE 1: NARRATIVA INICIAL (BOTAO CONTINUAR PARA O DESAFIO)
                  ------------------------------------------------------------- */}
              {stationStep === 'narrative' && (
                <div className="space-y-6 animate-fade-in py-4 text-center">
                  <div className="w-20 h-20 bg-chinese-gold/5 border border-chinese-gold/20 rounded-full flex items-center justify-center mx-auto text-4xl shadow-md">
                    {activeStation === 1 && "🍵"}
                    {activeStation === 2 && "🏮"}
                    {activeStation === 3 && "🎻"}
                    {activeStation === 4 && "🐲"}
                    {activeStation === 5 && "🥟"}
                  </div>
                  
                  <div className="space-y-3 max-w-lg mx-auto">
                    <h4 className="font-serif text-lg text-zinc-100">Pronto para o Desafio Interativo?</h4>
                    <p className="text-zinc-400 text-xs leading-relaxed">
                      Complete o desafio do diário para que Mei-Ling possa abrir os selos do pergaminho do grupo e validar seus conhecimentos.
                    </p>
                  </div>

                  {activeStation === 5 && (
                    <div className="mt-4 p-5 bg-red-900/30 border border-yellow-600/20 rounded-2xl text-left space-y-3 max-w-lg mx-auto animate-fade-in">
                      <h5 className="text-xs font-bold text-yellow-450 flex items-center gap-1.5 font-serif">
                        <span>🛡️ O Desafio de Honra: Ordem Unida Chinesa</span>
                      </h5>
                      <p className="text-[11px] text-zinc-300 leading-normal">
                        Como alunos militares do Mato Grosso, vocês demonstrarão a força da disciplina ao vivo na feira! Substituiremos a continência ocidental pela reverência tradicional chinesa <strong>Bao Quan Li</strong> (punho direito sob a palma esquerda, mostrando a sabedoria envolvendo a força) sob comandos em pinyin. Estude os termos com o seu grupo para a apresentação coletiva:
                      </p>
                      <div className="grid grid-cols-2 gap-2 text-[10px] font-mono">
                        <div className="bg-black/40 p-2.5 rounded border border-yellow-650/15">
                          <span className="text-yellow-400 font-bold block">Lìzhèng (Li-djêng):</span> Sentido! (Foco firme)
                        </div>
                        <div className="bg-black/40 p-2.5 rounded border border-yellow-650/15">
                          <span className="text-yellow-400 font-bold block">Shāoxī (Chao-chí):</span> Descansar!
                        </div>
                        <div className="bg-black/40 p-2.5 rounded border border-yellow-650/15">
                          <span className="text-yellow-400 font-bold block">Jìnglǐ (Djin-lí):</span> Reverência! (Respeito)
                        </div>
                        <div className="bg-black/40 p-2.5 rounded border border-yellow-650/15">
                          <span className="text-yellow-400 font-bold block">Lǐ bì (Li bi):</span> Desfazer Reverência!
                        </div>
                      </div>
                    </div>
                  )}

                  <button
                    onClick={() => {
                      audio.playWindChimes();
                      setStationStep('game');
                      if (activeStation === 4) {
                        startRhythmGame();
                      }
                    }}
                    className="bg-gradient-to-r from-chinese-gold to-yellow-600 hover:from-yellow-500 hover:to-chinese-gold text-black px-8 py-3 rounded-xl font-bold transition active:scale-95 shadow-lg"
                  >
                    Iniciar Mini-Desafio
                  </button>
                </div>
              )}

              {/* -------------------------------------------------------------
                  FASE 2: MINI-DESAFIOS INTERATIVOS
                  ------------------------------------------------------------- */}
              {stationStep === 'game' && (
                <div className="animate-fade-in py-2">
                  
                  {/* Desafio Capítulo 1: Ritual do Chá */}
                  {activeStation === 1 && (
                    <div className="space-y-6 text-center">
                      <h4 className="font-serif text-chinese-gold text-sm">Ritual Imperial: Sequencie as ações na ordem correta!</h4>
                      <div className="grid grid-cols-2 gap-4 max-w-md mx-auto">
                        {[
                          { id: 1, name: "1. Ferver Água Mineral 💧" },
                          { id: 2, name: "2. Adicionar Camellia Sinensis 🍃" },
                          { id: 3, name: "3. Descansar por 5 Minutos ⏳" },
                          { id: 4, name: "4. Servir na Xícara Imperial 🍵" }
                        ].map((act) => {
                          const clickIdx = teaSequence.indexOf(act.id);
                          return (
                            <button
                              key={act.id}
                              disabled={teaSuccess}
                              onClick={() => handleTeaClick(act.id)}
                              className={`p-4 rounded-xl border transition text-xs font-bold flex flex-col items-center justify-center gap-2 ${
                                clickIdx > -1
                                  ? 'bg-chinese-red border-chinese-red text-white'
                                  : 'bg-zinc-900 border-zinc-800 hover:border-chinese-gold/40 text-zinc-300'
                              }`}
                            >
                              <span>{act.name}</span>
                              {clickIdx > -1 && (
                                <span className="w-5 h-5 bg-chinese-gold text-black rounded-full flex items-center justify-center text-[10px] font-mono font-bold mt-1">
                                  {clickIdx + 1}
                                </span>
                              )}
                            </button>
                          );
                        })}
                      </div>

                      {teaSuccess && (
                        <p className="text-green-400 text-xs font-bold animate-pulse">✓ Ritual Concluído! Transicionando para o Quiz...</p>
                      )}
                    </div>
                  )}

                  {/* Desafio Capítulo 2: Zodíaco & Constelações */}
                  {activeStation === 2 && (
                    <div className="space-y-6 max-w-md mx-auto">
                      {!userZodiac ? (
                        <div className="bg-zinc-900 border border-zinc-800 p-5 rounded-2xl space-y-4">
                          <label className="text-xs text-zinc-400 font-mono uppercase tracking-wider block">Insira seu Ano de Nascimento:</label>
                          <div className="flex gap-2">
                            <input
                              type="number"
                              min="1900"
                              max="2030"
                              value={birthYear}
                              onChange={(e) => setBirthYear(e.target.value)}
                              placeholder="Ex: 2012"
                              className="flex-1 bg-zinc-950 border border-zinc-850 rounded-xl px-4 py-2 text-white focus:outline-none focus:border-chinese-gold transition"
                            />
                            <button
                              onClick={calculateZodiac}
                              className="bg-chinese-gold text-black font-bold px-5 rounded-xl text-xs hover:bg-yellow-500 transition"
                            >
                              Ver Signo
                            </button>
                          </div>
                        </div>
                      ) : (
                        <div className="space-y-6 text-center animate-fade-in">
                          <div className="bg-chinese-gold/5 border border-chinese-gold/25 p-4 rounded-xl">
                            <h5 className="text-sm font-serif text-chinese-gold font-bold">Seu Signo Chinês: {userZodiac.sign}</h5>
                            <p className="text-zinc-300 text-xs mt-1 leading-normal">{userZodiac.desc}</p>
                          </div>

                          <h4 className="font-serif text-chinese-gold text-xs">Toque nas 3 Estrelas para acender a Constelação:</h4>
                          <div className="flex justify-center gap-6 py-4">
                            {[1, 2, 3].map((starId) => {
                              const connected = starsConnected.includes(starId);
                              return (
                                <button
                                  key={starId}
                                  onClick={() => connectStar(starId)}
                                  className={`w-12 h-12 rounded-full border-2 flex items-center justify-center text-lg transition-all duration-300 ${
                                    connected
                                      ? 'bg-chinese-gold border-chinese-gold text-black scale-110 drop-shadow-[0_0_12px_rgba(255,215,0,0.6)]'
                                      : 'bg-zinc-900 border-zinc-700 text-zinc-600 hover:border-chinese-gold'
                                  }`}
                                >
                                  ⭐
                                </button>
                              );
                            })}
                          </div>
                          
                          {starsSuccess && (
                            <p className="text-green-400 text-xs font-bold animate-pulse">✓ Constelação Alinhada! Abrindo o Quiz...</p>
                          )}
                        </div>
                      )}
                    </div>
                  )}

                  {/* Desafio Capítulo 3: Ouvido Musical */}
                  {activeStation === 3 && (
                    <div className="space-y-6 animate-fade-in">
                      <div className="bg-red-900/40 border border-yellow-600/20 p-5 rounded-2xl text-center">
                        <h4 className="text-xs text-yellow-400 uppercase font-mono tracking-wider mb-2">Desafio Acústico Real</h4>
                        <p className="text-sm font-serif text-zinc-200">
                          Assista e ouça as duas apresentações tradicionais de cordas. Qual dos instrumentos corresponde ao melancólico **Erhu**?
                        </p>
                      </div>

                      <div className="grid grid-cols-1 sm:grid-cols-2 gap-6">
                        <div className="bg-red-900/30 border border-yellow-600/10 rounded-xl p-4 text-center space-y-3">
                          <h5 className="font-bold text-sm text-yellow-400 font-serif">Instrumento A</h5>
                          {currentStationConfig?.extraMediaUrl1 ? (
                            <div className="space-y-2">
                              <div className="relative aspect-video w-full bg-black rounded-lg overflow-hidden border border-yellow-600/20">
                                <iframe
                                  className="absolute inset-0 w-full h-full"
                                  src={currentStationConfig.extraMediaUrl1}
                                  title="Instrumento A"
                                  frameBorder="0"
                                  allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
                                  allowFullScreen
                                ></iframe>
                              </div>
                              <a
                                href={
                                  currentStationConfig.extraMediaUrl1.includes("youtube.com/embed/")
                                    ? `https://www.youtube.com/watch?v=${currentStationConfig.extraMediaUrl1.split("youtube.com/embed/")[1]?.split("?")[0] || ""}`
                                    : currentStationConfig.extraMediaUrl1
                                }
                                target="_blank"
                                rel="noopener noreferrer"
                                className="text-[10px] text-chinese-gold hover:text-yellow-400 font-bold underline inline-block"
                              >
                                Abrir vídeo no YouTube ↗
                              </a>
                            </div>
                          ) : (
                            <p className="text-xs text-red-200">Vídeo indisponível</p>
                          )}
                        </div>

                        <div className="bg-red-900/30 border border-yellow-600/10 rounded-xl p-4 text-center space-y-3">
                          <h5 className="font-bold text-sm text-yellow-400 font-serif">Instrumento B</h5>
                          {currentStationConfig?.extraMediaUrl2 ? (
                            <div className="space-y-2">
                              <div className="relative aspect-video w-full bg-black rounded-lg overflow-hidden border border-yellow-600/20">
                                <iframe
                                  className="absolute inset-0 w-full h-full"
                                  src={currentStationConfig.extraMediaUrl2}
                                  title="Instrumento B"
                                  frameBorder="0"
                                  allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
                                  allowFullScreen
                                ></iframe>
                              </div>
                              <a
                                href={
                                  currentStationConfig.extraMediaUrl2.includes("youtube.com/embed/")
                                    ? `https://www.youtube.com/watch?v=${currentStationConfig.extraMediaUrl2.split("youtube.com/embed/")[1]?.split("?")[0] || ""}`
                                    : currentStationConfig.extraMediaUrl2
                                }
                                target="_blank"
                                rel="noopener noreferrer"
                                className="text-[10px] text-chinese-gold hover:text-yellow-400 font-bold underline inline-block"
                              >
                                Abrir vídeo no YouTube ↗
                              </a>
                            </div>
                          ) : (
                            <p className="text-xs text-red-200">Vídeo indisponível</p>
                          )}
                        </div>
                      </div>

                      {!earGameAnswer ? (
                        <div className="space-y-2 pt-4">
                          <label className="text-xs text-yellow-400 font-mono uppercase tracking-wider block text-center font-bold">Sua Escolha:</label>
                          <div className="flex gap-4 max-w-sm mx-auto">
                            <button
                              onClick={() => handleEarSubmit('pipa')}
                              className="flex-1 bg-red-950 border border-yellow-600/30 hover:border-yellow-400 py-3 rounded-xl font-bold transition text-xs text-zinc-300 active:scale-95"
                            >
                              Instrumento A (Pipa)
                            </button>
                            <button
                              onClick={() => handleEarSubmit('erhu')}
                              className="flex-1 bg-red-950 border border-yellow-600/50 hover:border-yellow-400 py-3 rounded-xl font-bold transition text-xs text-yellow-450 active:scale-95 shadow-md"
                            >
                              Instrumento B (Erhu)
                            </button>
                          </div>
                        </div>
                      ) : (
                        <div className="bg-red-900/30 border border-yellow-600/25 p-5 rounded-xl space-y-4 text-center max-w-md mx-auto">
                          <p className="text-xs text-zinc-300 leading-relaxed italic">
                            {earGameFeedback}
                          </p>
                          <button
                            onClick={() => {
                              setShowVideoModal(true);
                              setStationStep('quiz');
                            }}
                            className="bg-gradient-to-r from-chinese-red to-red-700 text-white text-xs py-2.5 px-6 rounded-lg font-bold hover:from-red-600 hover:to-chinese-red transition"
                          >
                            Ir para o Quiz do Capítulo
                          </button>
                        </div>
                      )}
                    </div>
                  )}

                  {/* Desafio Capítulo 4: Ritmo do Dragão */}
                  {activeStation === 4 && (
                    <div className="space-y-6 text-center max-w-lg mx-auto animate-fade-in">
                      
                      {/* Embed Real de Dança do Dragão dos Alunos */}
                      <div className="space-y-2">
                        <span className="text-[10px] text-yellow-400 font-mono uppercase tracking-widest block font-bold">Mídia Real do Grupo: Ritmo da Dança</span>
                        <div className="relative aspect-video w-full max-w-sm mx-auto bg-black rounded-xl overflow-hidden border border-yellow-600/30 shadow-md">
                          <iframe
                            className="absolute inset-0 w-full h-full pointer-events-none"
                            src="https://www.youtube.com/embed/nU2Wos-v680?autoplay=1&mute=1&loop=1&playlist=nU2Wos-v680&controls=0"
                            title="Dança do Dragão em Loop"
                            frameBorder="0"
                            allow="autoplay; encrypted-media"
                          ></iframe>
                        </div>
                      </div>

                      <h5 className="text-xs text-yellow-400 font-mono uppercase tracking-widest font-bold mt-4">{rhythmMsg}</h5>
                      
                      <div className="relative w-40 h-40 mx-auto bg-red-950/60 border border-yellow-600/35 rounded-full flex items-center justify-center overflow-hidden shadow-inner">
                        
                        {/* Target Ring */}
                        <div className="absolute w-20 h-20 border-2 border-yellow-500/50 rounded-full pointer-events-none"></div>
                        
                        {/* Animating Ring */}
                        <div 
                          className="absolute border-2 border-yellow-400 rounded-full transition-transform duration-75 pointer-events-none"
                          style={{
                            width: '80px',
                            height: '80px',
                            transform: `scale(${circleScale})`,
                            opacity: 0.8
                          }}
                        ></div>
                        
                        <div className="text-4xl z-10 pointer-events-none select-none">🥁</div>
                      </div>

                      <button
                        onClick={hitDrum}
                        className="bg-gradient-to-r from-chinese-gold to-yellow-600 hover:from-yellow-500 hover:to-chinese-gold text-black font-bold px-8 py-3 rounded-xl text-xs transition active:scale-95 shadow-lg block mx-auto"
                      >
                        Bater Tambor 🥁
                      </button>
                    </div>
                  )}

                  {/* Desafio Capítulo 5: Pastel Fritador */}
                  {activeStation === 5 && (
                    <div className="space-y-6 text-center max-w-md mx-auto animate-fade-in">
                      <h5 className="text-xs text-yellow-450 font-mono uppercase tracking-widest font-bold">{pastelFryingMsg}</h5>

                      {pastelFryingState === 'idle' && (
                        <div className="flex gap-4 justify-center py-4">
                          <button
                            onClick={() => startFrying('queijo')}
                            className="bg-red-950/70 border border-yellow-600/35 hover:border-yellow-400 p-4 rounded-xl text-xs font-bold text-zinc-300 transition active:scale-95 shadow-md"
                          >
                            🧀 Recheio de Queijo
                          </button>
                          <button
                            onClick={() => startFrying('carne')}
                            className="bg-red-950/70 border border-yellow-600/35 hover:border-yellow-400 p-4 rounded-xl text-xs font-bold text-zinc-300 transition active:scale-95 shadow-md"
                          >
                            🥩 Recheio de Carne
                          </button>
                        </div>
                      )}

                      {pastelFryingState === 'frying' && (
                        <div className="space-y-4 py-4 animate-pulse">
                          <p className="text-xs text-yellow-450">Fritando pastel de {pastelFilling === 'queijo' ? 'Queijo 🧀' : 'Carne 🥩'}...</p>
                          <div className="w-full bg-red-950 border border-yellow-650/30 rounded-full h-4 overflow-hidden relative">
                            {/* Green Zone (Ideal Point: 60-85%) */}
                            <div className="absolute left-[60%] w-[25%] bg-emerald-500/40 h-full"></div>
                            <div 
                              className="bg-gradient-to-r from-yellow-500 to-red-650 h-full transition-all duration-100"
                              style={{ width: `${pastelFryingProgress}%` }}
                            ></div>
                          </div>
                          <div className="text-xs text-zinc-400">Zona Ideal: Dourada/Crocante (Verde)</div>
                          <button
                            onClick={pullPastel}
                            className="bg-gradient-to-r from-yellow-500 to-yellow-600 hover:from-yellow-400 hover:to-yellow-500 text-black font-bold px-6 py-2.5 rounded-xl text-xs active:scale-95 transition shadow-lg"
                          >
                            Retirar da Fritadeira 🥟
                          </button>
                        </div>
                      )}

                      {(pastelFryingState === 'burnt' || pastelFryingState === 'soggy') && (
                        <button
                          onClick={() => setPastelFryingState('idle')}
                          className="bg-red-900/60 hover:bg-red-800 text-white font-bold px-6 py-2.5 rounded-xl text-xs active:scale-95 transition flex items-center gap-2 mx-auto border border-yellow-650/30"
                        >
                          <RefreshCw className="w-3.5 h-3.5" />
                          <span>Tentar Novamente</span>
                        </button>
                      )}
                    </div>
                  )}

                </div>
              )}

              {/* -------------------------------------------------------------
                  FASE 3: PERGUNTAS DO QUIZ PEDAGÓGICO
                  ------------------------------------------------------------- */}
              {stationStep === 'quiz' && (() => {
                const q = questions.find(item => item.id_grupo === activeStation);
                if (!q) return <p className="text-zinc-500 text-center py-4">Carregando pergunta cadastrada pelo professor...</p>;
                return (
                  <div className="space-y-6 animate-fade-in">
                    <div className="bg-zinc-900 border border-zinc-850 p-5 rounded-xl">
                      <p className="text-xs text-zinc-400 uppercase font-mono tracking-wider mb-2">Tema: {q.tema}</p>
                      <h4 className="text-base sm:text-lg font-serif text-zinc-200 leading-snug">{q.pergunta}</h4>
                    </div>

                    <div className="grid grid-cols-1 gap-3">
                      {q.opcoes.map((option, i) => {
                        const isSelected = selectedOption === option;
                        return (
                          <button
                            key={i}
                            disabled={selectedOption !== null}
                            onClick={() => handleQuizAnswer(option, q, activeStation)}
                            className={`w-full text-left p-4 rounded-xl border text-sm font-medium transition duration-200 flex items-center justify-between ${
                              selectedOption !== null
                                ? option === q.resposta_correta
                                  ? 'bg-green-950/40 border-green-500 text-green-400'
                                  : isSelected
                                    ? 'bg-red-950/40 border-red-500 text-red-400'
                                    : 'bg-zinc-900/40 border-zinc-900 text-zinc-650'
                                : 'bg-zinc-900 border-zinc-800 hover:border-chinese-gold/50 text-zinc-300 hover:bg-zinc-900/80'
                            }`}
                          >
                            <span>{option}</span>
                            {selectedOption !== null && option === q.resposta_correta && <Check className="w-4 h-4 text-green-400" />}
                            {selectedOption !== null && isSelected && option !== q.resposta_correta && <X className="w-4 h-4 text-red-400" />}
                          </button>
                        );
                      })}
                    </div>
                  </div>
                );
              })()}

            </div>
          )}

          {allCompleted && (
            /* TELA FINAL DE AVALIAÇÃO */
            <div className="bg-red-950/90 border border-yellow-600/35 chinese-border rounded-3xl p-6 sm:p-8 text-center space-y-8 animate-fade-in shadow-2xl relative z-10">
              <div className="flex justify-center">
                <div className="w-20 h-20 bg-yellow-500/10 border border-yellow-550/30 rounded-full flex items-center justify-center shadow-lg animate-bounce">
                  <Award className="w-10 h-10 text-yellow-400" />
                </div>
              </div>

              <div className="space-y-2">
                <h2 className="text-3xl font-serif text-yellow-450">Jornada Concluída!</h2>
                <p className="text-zinc-300 text-sm">
                  Parabéns! Você viajou por toda a trilha pedagógica da Feira das Nações.
                </p>
                <div className="inline-block bg-yellow-500/10 border border-yellow-550/30 px-6 py-2 rounded-full mt-4 text-yellow-400 font-mono font-bold text-sm shadow-md">
                  Sua Pontuação Final: {score} Acertos no Quiz • {xp} XP Acumulados
                </div>
              </div>

              {activeProverb && (
                <div className="bg-red-900/40 border border-yellow-600/20 rounded-xl p-4 italic text-zinc-300 text-xs max-w-lg mx-auto">
                  <p>"{activeProverb}"</p>
                </div>
              )}

              {!isSaved ? (
                <div className="space-y-6 max-w-md mx-auto pt-4 border-t border-zinc-900">
                  <div className="space-y-2">
                    <label className="text-sm font-semibold text-zinc-300 block">
                      Avalie o trabalho e a feira dos alunos (Estrelas):
                    </label>
                    <div className="flex justify-center items-center gap-1.5">
                      {[1, 2, 3, 4, 5].map((starValue) => {
                        const isActive = (hoveredStars || stars) >= starValue;
                        return (
                          <button
                            key={starValue}
                            onClick={() => setStars(starValue)}
                            onMouseEnter={() => setHoveredStars(starValue)}
                            onMouseLeave={() => setHoveredStars(0)}
                            className="p-1 focus:outline-none transition-transform active:scale-125"
                          >
                            <Star 
                              className={`w-7 h-7 transition-colors ${
                                isActive 
                                  ? 'fill-chinese-gold text-chinese-gold drop-shadow-[0_0_6px_rgba(255,215,0,0.5)]' 
                                  : 'text-zinc-755'
                              }`} 
                            />
                          </button>
                        );
                      })}
                    </div>
                  </div>

                  <div className="space-y-1">
                    <label className="text-[11px] text-zinc-550 font-mono uppercase tracking-wider block text-left">
                      Deixe uma mensagem ou opinião para a turma (Opcional):
                    </label>
                    <textarea
                      rows={3}
                      value={feedbackText}
                      onChange={(e) => setFeedbackText(e.target.value)}
                      placeholder="Gostei muito dos vídeos do Pastel e do Kung Fu..."
                      className="w-full bg-zinc-900 border border-zinc-800 rounded-xl p-4 text-xs text-white placeholder-zinc-700 focus:outline-none focus:border-chinese-gold transition resize-none"
                    />
                  </div>

                  <button
                    onClick={handleFinishedEvaluation}
                    disabled={stars === 0}
                    className={`w-full py-3.5 px-6 rounded-xl font-bold flex items-center justify-center gap-2 transition-all ${
                      stars === 0 
                        ? 'bg-zinc-800 text-zinc-500 cursor-not-allowed border border-zinc-700/50'
                        : 'bg-gradient-to-r from-chinese-gold to-yellow-600 hover:from-yellow-500 hover:to-chinese-gold text-black active:scale-95 shadow-[0_4px_15px_rgba(255,215,0,0.25)]'
                    }`}
                  >
                    <MessageSquare className="w-5 h-5" />
                    <span>Enviar Avaliação & Entrar no Hall da Fama</span>
                  </button>
                </div>
              ) : (
                <div className="space-y-4 max-w-sm mx-auto pt-4 animate-fade-in">
                  <div className="flex items-center justify-center gap-2 text-green-400 font-bold">
                    <Check className="w-5 h-5" />
                    <span>Seu nome foi registrado com sucesso!</span>
                  </div>
                  <button
                    onClick={() => {
                      localStorage.removeItem('mock_user');
                      window.location.reload();
                    }}
                    className="w-full bg-zinc-800 hover:bg-zinc-700 text-white font-bold py-3 rounded-xl transition text-sm"
                  >
                    Entrar com Outro Usuário
                  </button>
                </div>
              )}
            </div>
          )}
        </section>

        {/* Lado Direito: Hall da Fama (4 Colunas) */}
        <section className="lg:col-span-4 bg-red-950/85 border border-yellow-600/25 chinese-border-double rounded-3xl p-6 shadow-xl space-y-6 z-10">
          <div className="flex items-center gap-2 border-b border-red-900 pb-3">
            <Trophy className="w-5 h-5 text-yellow-400" />
            <h2 className="font-serif text-lg text-yellow-450">Hall da Fama</h2>
          </div>

          <div className="space-y-3 max-h-[420px] overflow-y-auto pr-1">
            {leaderboard.length === 0 ? (
              <p className="text-center text-xs text-red-300/40 font-mono py-8">
                Nenhuma lenda registrada ainda. Seja o primeiro!
              </p>
            ) : (
              leaderboard.map((student, idx) => (
                <div 
                  key={student.uid}
                  className={`p-3 rounded-xl border flex items-center justify-between transition ${
                    student.uid === user?.uid 
                      ? 'bg-yellow-500/10 border-yellow-550/40 shadow-inner' 
                      : 'bg-red-900/30 border-red-900/60'
                  }`}
                >
                  <div className="flex items-center gap-3">
                    <span className="text-base font-mono w-5 text-center">
                      {idx === 0 && '🥇'}
                      {idx === 1 && '🥈'}
                      {idx === 2 && '🥉'}
                      {idx > 2 && `${idx + 1}.`}
                    </span>
                    <div>
                      <div className="text-xs font-bold text-zinc-200">{student.name}</div>
                      <div className="text-[10px] text-red-200/50 font-mono leading-tight">{student.email}</div>
                    </div>
                  </div>

                  <div className="text-right space-y-0.5">
                    <div className="text-xs font-mono font-bold text-yellow-400">{student.xp} XP</div>
                    <div className="text-[9px] text-red-200/50 font-mono">Quiz: {student.score}/5</div>
                  </div>
                </div>
              ))
            )}
          </div>

          <div className="bg-red-900/40 border border-yellow-600/20 rounded-xl p-3.5 space-y-2">
            <h5 className="text-xs font-bold text-yellow-400 flex items-center gap-1.5">
              <ShieldAlert className="w-3.5 h-3.5" />
              <span>Notas do Professor Johnny:</span>
            </h5>
            <p className="text-[11px] text-red-200/70 leading-normal font-sans">
              O projeto valoriza o esforço e a imersão pedagógica! A pontuação no quiz confere XP, mas a interação total pelas 5 estações e feedbacks sinceros coroa o aluno com a medalha de Lenda.
            </p>
          </div>
        </section>
      </main>

      {/* Video Modal (YouTube Feedback) */}
      {showVideoModal && (
        <div className="fixed inset-0 z-50 bg-black/95 backdrop-blur-sm flex items-center justify-center p-4">
          <div className="bg-chinese-gray border border-chinese-gold/20 rounded-2xl w-full max-w-2xl overflow-hidden shadow-2xl animate-fade-in my-8">
            <div className="px-5 py-3 border-b border-zinc-800 flex items-center justify-between">
              <div className="flex items-center gap-2">
                <span className="w-5 h-5 rounded-full bg-chinese-gold/10 text-chinese-gold flex items-center justify-center border border-chinese-gold/30 text-xs">🎬</span>
                <span className="font-serif font-bold text-sm">Trabalho do Grupo de Alunos</span>
              </div>
            </div>

            <div className="p-5 space-y-5">
              {currentVideoUrl && (
                <div className="space-y-2">
                  <div className="relative aspect-video w-full bg-black rounded-xl overflow-hidden border border-zinc-800">
                    <iframe
                      className="absolute inset-0 w-full h-full"
                      src={currentVideoUrl}
                      title="Vídeo de Feedback dos Alunos"
                      frameBorder="0"
                      allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share"
                      allowFullScreen
                    ></iframe>
                  </div>
                  <div className="flex justify-center">
                    <a
                      href={
                        currentVideoUrl.includes("youtube.com/embed/")
                          ? `https://www.youtube.com/watch?v=${currentVideoUrl.split("youtube.com/embed/")[1]?.split("?")[0] || ""}`
                          : currentVideoUrl.includes("drive.google.com/file/d/")
                          ? `https://drive.google.com/file/d/${currentVideoUrl.split("drive.google.com/file/d/")[1]?.split("/")[0] || ""}/view`
                          : currentVideoUrl
                      }
                      target="_blank"
                      rel="noopener noreferrer"
                      className="text-xs text-chinese-gold hover:text-yellow-400 font-bold underline flex items-center gap-1 active:scale-95 transition"
                    >
                      📺 Não carregou? Assistir diretamente no YouTube ou Drive ↗
                    </a>
                  </div>
                </div>
              )}

              {(() => {
                const activeQ = questions.find(q => q.id_grupo === activeStation);
                return activeQ?.texto_explicativo ? (
                  <div className="bg-zinc-900 border border-zinc-800 rounded-xl p-4 text-zinc-300 text-xs leading-relaxed">
                    <h5 className="font-bold text-[10px] text-chinese-gold uppercase tracking-wider mb-1">📖 Card Explicativo do Grupo</h5>
                    <p className="whitespace-pre-wrap">{activeQ.texto_explicativo}</p>
                  </div>
                ) : null;
              })()}

              <div className="space-y-1">
                <h4 className="text-[10px] font-mono text-chinese-gold uppercase tracking-wider font-semibold">Explicativo Pedagógico</h4>
                <p className="text-zinc-400 text-xs leading-normal">
                  {currentVideoUrl 
                    ? "Assista ao vídeo e leia as explicações preparadas pelos alunos do 7º Ano D. Ao terminar, clique em continuar para registrar seus pontos de experiência."
                    : "Leia as explicações preparadas pelos alunos do 7º Ano D. Ao terminar, clique em continuar para registrar seus pontos de experiência."
                  }
                </p>
              </div>
            </div>

            <div className="px-5 py-3 border-t border-zinc-800 bg-zinc-950/50 flex justify-end">
              <button
                onClick={() => {
                  setShowVideoModal(false);
                  setSelectedOption(null);
                  setActiveStation(null); // Retorna para o mapa
                }}
                className="bg-gradient-to-r from-chinese-red to-red-700 hover:from-red-600 hover:to-chinese-red text-white py-2 px-5 rounded-xl font-bold text-xs transition flex items-center gap-1.5 active:scale-95 shadow-md"
              >
                <span>Fechar e Coletar XP</span>
                <ChevronRight className="w-3.5 h-3.5" />
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Modal de Recompensa Final (Gerador de Carta Mítica) */}
      {showRewardModal && (
        <div className="fixed inset-0 z-50 bg-black/95 backdrop-blur-md overflow-y-auto p-4 sm:p-6 flex flex-col justify-between">
          {/* Header */}
          <div className="max-w-3xl w-full mx-auto pb-4 border-b border-zinc-800 flex justify-between items-center">
            <div className="flex items-center gap-2">
              <span className="text-2xl">🏆</span>
              <div className="text-left">
                <h3 className="font-serif font-bold text-yellow-450 text-base sm:text-lg">Prêmio Imperial: Carta Mítica</h3>
                <p className="text-[10px] text-zinc-500 font-mono tracking-wider uppercase">Jornada de Superação e Conhecimento</p>
              </div>
            </div>
            
            <button
              onClick={() => {
                stopCamera();
                setShowRewardModal(false);
              }}
              className="bg-zinc-900 border border-zinc-800 hover:border-chinese-red hover:text-chinese-red text-zinc-400 font-mono text-xs py-2 px-4 rounded-xl transition uppercase tracking-wider"
            >
              Ver Hall da Fama
            </button>
          </div>

          {/* Conteúdo Principal */}
          <div className="flex-1 flex flex-col items-center justify-center py-6 max-w-md w-full mx-auto space-y-6">
            
            {/* Título de Instruções */}
            <div className="text-center space-y-1">
              <h4 className="font-serif text-sm text-zinc-200">
                {generatedCardUrl ? "Sua Carta Mítica Está Pronta!" : "Registre seu rosto no Templo da Sabedoria"}
              </h4>
              <p className="text-xs text-zinc-400 leading-normal">
                {generatedCardUrl 
                  ? "Baixe e guarde seu card personalizado como prova de sua vitória e aprendizado."
                  : "Tire uma foto ou carregue uma imagem para a Sabedoria do Diário de Chen compor sua carta."
                }
              </p>
              <div className="inline-block bg-yellow-500/10 border border-yellow-550/20 px-3 py-1 rounded-full text-[10px] font-mono text-yellow-400 mt-2 font-bold animate-pulse">
                Tentativas Utilizadas: {generationAttempts} de 3
              </div>
            </div>

            {/* Renderização da Câmera / Upload */}
            {!generatedCardUrl && (
              <div className="w-full bg-zinc-900 border border-zinc-800 rounded-2xl p-6 text-center space-y-4 shadow-xl">
                {isCameraActive ? (
                  <div className="space-y-4">
                    <div className="relative aspect-square w-full max-w-xs mx-auto bg-black rounded-xl overflow-hidden border border-yellow-500/30">
                      <video 
                        ref={videoRef} 
                        autoPlay 
                        playsInline 
                        className="absolute inset-0 w-full h-full object-cover scale-x-[-1]" 
                      />
                    </div>
                    <div className="flex gap-3 justify-center">
                      <button
                        onClick={capturePhoto}
                        className="bg-gradient-to-r from-chinese-gold to-yellow-600 hover:from-yellow-500 hover:to-chinese-gold text-black font-bold py-2.5 px-6 rounded-xl text-xs active:scale-95 transition shadow-lg"
                      >
                        Tirar Foto 📸
                      </button>
                      <button
                        onClick={stopCamera}
                        className="bg-zinc-800 hover:bg-zinc-700 text-zinc-300 font-bold py-2.5 px-4 rounded-xl text-xs active:scale-95 transition border border-zinc-700"
                      >
                        Cancelar
                      </button>
                    </div>
                  </div>
                ) : (
                  <div className="space-y-4 py-6">
                    <div className="w-16 h-16 bg-yellow-500/5 border border-yellow-500/25 rounded-full flex items-center justify-center mx-auto text-2xl shadow-inner animate-pulse">
                      📷
                    </div>
                    <p className="text-xs text-zinc-400">
                      Você pode usar a câmera frontal do celular ou carregar uma imagem da sua galeria de fotos.
                    </p>
                    <div className="flex flex-col gap-3 max-w-xs mx-auto pt-2">
                      <button
                        onClick={startCamera}
                        disabled={generationAttempts >= 3}
                        className={`w-full font-bold py-3 px-6 rounded-xl text-xs active:scale-95 transition flex items-center justify-center gap-2 shadow-md ${
                          generationAttempts >= 3 
                            ? 'bg-zinc-800 text-zinc-500 cursor-not-allowed border border-zinc-700/50' 
                            : 'bg-gradient-to-r from-chinese-gold to-yellow-600 hover:from-yellow-500 hover:to-chinese-gold text-black'
                        }`}
                      >
                        <span>Usar Câmera 📸</span>
                      </button>
                      
                      <label 
                        className={`w-full font-bold py-3 px-6 rounded-xl text-xs active:scale-95 transition flex items-center justify-center gap-2 border border-zinc-850 shadow-md cursor-pointer justify-center text-center ${
                          generationAttempts >= 3 
                            ? 'bg-zinc-800 text-zinc-500 cursor-not-allowed border border-zinc-700/50 pointer-events-none' 
                            : 'bg-zinc-900 text-zinc-200 hover:bg-zinc-850 hover:border-yellow-600/30'
                        }`}
                      >
                        <span>Carregar Imagem 📤</span>
                        <input
                          type="file"
                          accept="image/*"
                          disabled={generationAttempts >= 3}
                          onChange={handleImageUpload}
                          className="hidden"
                        />
                      </label>

                      <button
                        onClick={generateWithoutPhoto}
                        disabled={generationAttempts >= 3}
                        className={`w-full font-bold py-3 px-6 rounded-xl text-xs active:scale-95 transition flex items-center justify-center gap-2 border border-zinc-850 shadow-md ${
                          generationAttempts >= 3 
                            ? 'bg-zinc-800 text-zinc-500 cursor-not-allowed border border-zinc-700/50' 
                            : 'bg-zinc-900 text-zinc-200 hover:bg-zinc-850 hover:border-yellow-600/30'
                        }`}
                      >
                        <span>Gerar sem Foto 🏮</span>
                      </button>
                    </div>
                  </div>
                )}
              </div>
            )}

            {/* Renderização do Card Gerado */}
            {generatedCardUrl && (
              <div className="w-full flex flex-col items-center space-y-6 animate-scale-up">
                <div className="relative max-w-xs w-full bg-zinc-900 rounded-2xl overflow-hidden border-2 border-yellow-500 shadow-2xl">
                  <img 
                    src={generatedCardUrl} 
                    className="w-full h-auto object-contain" 
                    alt="Sua Carta Mítica" 
                  />
                </div>

                {/* Seleção do Fundo do Card */}
                <div className="w-full max-w-xs space-y-2 animate-fade-in">
                  <span className="text-[10px] text-zinc-500 font-mono uppercase block text-left">Escolha a Atmosfera Chinesa do Card:</span>
                  <div className="flex flex-wrap gap-1.5 justify-center">
                    {[
                      { id: 'dragoes_fenix', name: 'Dragões & Fênix 🐉' },
                      { id: 'explosiva', name: 'Energia 💥' },
                      { id: 'cultura_cha', name: 'Chá 🍵' },
                      { id: 'instrumentos', name: 'Música 🎵' },
                      { id: 'festival', name: 'Lanternas 🏮' }
                    ].map(bg => (
                      <button
                        key={bg.id}
                        onClick={() => setSelectedCardBg(bg.id)}
                        className={`px-2.5 py-1.5 rounded-lg text-[9px] font-bold border transition ${
                          selectedCardBg === bg.id
                            ? 'bg-yellow-500 border-yellow-500 text-black shadow-md'
                            : 'bg-zinc-900 border-zinc-850 text-zinc-400 hover:border-yellow-600/30'
                        }`}
                      >
                        {bg.name}
                      </button>
                    ))}
                  </div>
                </div>

                {/* Ajustes de Fusão de Rosto (Controle Manual de Alinhamento) - Apenas se tiver foto */}
                {capturedImage && capturedImage !== "none" && (
                  <div className="w-full max-w-xs bg-zinc-950/60 border border-zinc-900/60 p-4 rounded-xl space-y-3.5 animate-fade-in">
                    <span className="text-[10px] text-zinc-400 font-mono uppercase block text-left font-bold tracking-wider">Ajustes da Fusão de Rosto:</span>
                    
                    <div className="space-y-1">
                      <div className="flex justify-between text-[10px] font-mono text-zinc-400">
                        <span>Altura do Rosto (Y):</span>
                        <span className="text-yellow-500 font-bold">{faceYOffset > 0 ? `+${faceYOffset}` : faceYOffset}px</span>
                      </div>
                      <input
                        type="range"
                        min="-60"
                        max="60"
                        step="2"
                        value={faceYOffset}
                        onChange={(e) => setFaceYOffset(parseInt(e.target.value))}
                        className="w-full h-1 bg-zinc-800 rounded-lg appearance-none cursor-pointer accent-yellow-500"
                      />
                    </div>

                    <div className="space-y-1">
                      <div className="flex justify-between text-[10px] font-mono text-zinc-400">
                        <span>Zoom do Rosto:</span>
                        <span className="text-yellow-500 font-bold">{faceZoom.toFixed(2)}x</span>
                      </div>
                      <input
                        type="range"
                        min="0.6"
                        max="1.6"
                        step="0.05"
                        value={faceZoom}
                        onChange={(e) => setFaceZoom(parseFloat(e.target.value))}
                        className="w-full h-1 bg-zinc-800 rounded-lg appearance-none cursor-pointer accent-yellow-500"
                      />
                    </div>
                  </div>
                )}
                
                <div className="flex flex-col gap-3 w-full max-w-xs">
                  <button
                    onClick={downloadCard}
                    className="w-full bg-gradient-to-r from-emerald-500 to-green-600 hover:from-emerald-400 hover:to-green-500 text-white font-bold py-3.5 px-6 rounded-xl text-xs active:scale-95 transition shadow-lg flex items-center justify-center gap-2"
                  >
                    <span>Baixar Carta Mítica 🏆</span>
                  </button>

                  {generationAttempts < 3 ? (
                    <div className="space-y-3">
                      <div className="text-center text-[10px] text-zinc-550 font-mono">
                        Gostou do resultado? Se quiser, você pode tentar gerar mais {3 - generationAttempts} vezes.
                      </div>
                      <div className="flex flex-col gap-2">
                        <div className="flex gap-2">
                          <button
                            onClick={startCamera}
                            className="flex-1 bg-zinc-900 border border-zinc-800 hover:border-yellow-500 text-zinc-300 font-bold py-2.5 px-4 rounded-xl text-xs active:scale-95 transition flex items-center justify-center gap-1"
                          >
                            Câmera 🔄
                          </button>
                          <label className="flex-1 bg-zinc-900 border border-zinc-800 hover:border-yellow-500 text-zinc-300 font-bold py-2.5 px-4 rounded-xl text-xs active:scale-95 transition flex items-center justify-center gap-1 cursor-pointer justify-center text-center">
                            <span>Upload 📤</span>
                            <input
                              type="file"
                              accept="image/*"
                              onChange={handleImageUpload}
                              className="hidden"
                            />
                          </label>
                        </div>
                        <button
                          onClick={generateWithoutPhoto}
                          className="w-full bg-zinc-900 border border-zinc-800 hover:border-yellow-500 text-zinc-300 font-bold py-2 px-4 rounded-xl text-xs active:scale-95 transition flex items-center justify-center gap-1"
                        >
                          Gerar sem Foto 🏮
                        </button>
                      </div>
                    </div>
                  ) : (
                    <div className="bg-red-950/40 border border-red-900/30 p-3 rounded-xl text-center text-[11px] text-red-300/80 leading-normal font-sans">
                      ⚠️ Você atingiu seu limite de 3 tentativas! Salve o card definitivo gerado acima.
                    </div>
                  )}
                </div>
              </div>
            )}
          </div>

          {/* Footer */}
          <div className="max-w-xl w-full mx-auto text-center pt-4 border-t border-zinc-900">
            <p className="text-[11px] text-zinc-550 leading-relaxed max-w-md mx-auto">
              "A sabedoria ancestral nos lembra que a vitória é conquistada por meio do respeito e da persistência."
            </p>
            <button
              onClick={() => {
                stopCamera();
                setShowRewardModal(false);
              }}
              className="mt-3 bg-gradient-to-r from-chinese-gold to-yellow-600 hover:from-yellow-500 hover:to-chinese-gold text-black py-3 px-8 rounded-xl font-bold text-xs transition active:scale-95 shadow-md mx-auto block"
            >
              Concluir & Ver Hall da Fama
            </button>
          </div>
        </div>
      )}

      {/* Animação do Dragão Imperial */}
      {showDragon && (
        <div className="dragon-anim fixed top-1/3 left-0 z-50 pointer-events-none text-7xl select-none">
          🐉🐉🐉✨
        </div>
      )}
    </div>
  );
}
