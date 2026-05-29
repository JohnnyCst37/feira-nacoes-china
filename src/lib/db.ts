import { 
  collection, 
  getDocs, 
  doc, 
  setDoc,
  getDoc
} from "firebase/firestore";
import { db, isMockMode } from "./firebase";

export interface QuestionCard {
  id: string; // ex: grupo_1
  id_grupo: number;
  tema: string;
  pergunta: string;
  opcoes: string[];
  resposta_correta: string;
  video_feedback_url: string;
  texto_explicativo?: string;
}

export interface StudentProgress {
  uid: string;
  name: string;
  email: string;
  score: number;
  completed: boolean;
  stars: number;
  feedbackText?: string;
  lastActive: string;
  xp: number;
  badges: string[];
  unlockedCards?: string[]; // Para compatibilidade
  avatarIndex?: number;     // Para compatibilidade
}

export interface StationConfig {
  stationId: number; // 1 a 5
  backgroundImageUrl: string;
  backgroundVideoUrl: string;
  melodyUrl: string;
  customTitle: string;
  customSubtitle: string;
  extraMediaUrl1?: string; // Mídia extra 1 (ex: Instrumento A)
  extraMediaUrl2?: string; // Mídia extra 2 (ex: Instrumento B)
}

export interface AppSettings {
  soundtrackUrl: string;
  soundtrackVolume: number;
}

const defaultAppSettings: AppSettings = {
  soundtrackUrl: "/audio/traditional_chinese_music.mp3",
  soundtrackVolume: 0.25
};

// Configurações padrão de atmosfera por estação (imagens/videos de fundo e melodias)
const defaultStationConfigs: StationConfig[] = [
  {
    stationId: 1,
    backgroundImageUrl: "",
    backgroundVideoUrl: "",
    melodyUrl: "",
    customTitle: "Jardim Imperial do Chá",
    customSubtitle: "Sinta o aroma do chá e a névoa das montanhas de Hangzhou",
    extraMediaUrl1: "",
    extraMediaUrl2: ""
  },
  {
    stationId: 2,
    backgroundImageUrl: "",
    backgroundVideoUrl: "",
    melodyUrl: "",
    customTitle: "Céu Estrelado de Lanternas",
    customSubtitle: "Veja os balões de luz subindo ao cosmos",
    extraMediaUrl1: "",
    extraMediaUrl2: ""
  },
  {
    stationId: 3,
    backgroundImageUrl: "",
    backgroundVideoUrl: "",
    melodyUrl: "",
    customTitle: "Templo Acústico de Cordas",
    customSubtitle: "Vibração harmônica pentatônica de Guzheng & Erhu",
    extraMediaUrl1: "https://www.youtube.com/embed/z12M1bI06iM", // Som Pipa
    extraMediaUrl2: "https://www.youtube.com/embed/R6jYq2-z1c0"  // Som Erhu
  },
  {
    stationId: 4,
    backgroundImageUrl: "",
    backgroundVideoUrl: "",
    melodyUrl: "",
    customTitle: "Pátio da Dança Cósmica",
    customSubtitle: "Acompanhe o ritmo dos tambores e a dança do dragão",
    extraMediaUrl1: "",
    extraMediaUrl2: ""
  },
  {
    stationId: 5,
    backgroundImageUrl: "",
    backgroundVideoUrl: "",
    melodyUrl: "",
    customTitle: "Barraca Gastronômica & Combate",
    customSubtitle: "Prepare pastéis crocantes e aprenda a filosofia Shaolin",
    extraMediaUrl1: "",
    extraMediaUrl2: ""
  }
];

// Perguntas padrão baseadas nas estações de narrativa
const defaultQuestions: QuestionCard[] = [
  {
    id: "grupo_1",
    id_grupo: 1,
    tema: "Chá Imperial e Imigração",
    pergunta: "Em qual local histórico do Rio de Janeiro a imigração chinesa iniciou o cultivo do chá no início do século XIX, sob incentivo de Dom João VI?",
    opcoes: ["Jardim Botânico do Rio", "Parque Lage", "Pão de Açúcar", "Floresta da Tijuca"],
    resposta_correta: "Jardim Botânico do Rio",
    video_feedback_url: "https://www.youtube.com/embed/z12M1bI06iM",
    texto_explicativo: "O chá da Índia e da China começou a ser plantado no Jardim Botânico e na Fazenda Real de Santa Cruz por colonos chineses contratados em 1812. Esse cultivo é considerado o marco inicial da presença chinesa oficial em solo brasileiro."
  },
  {
    id: "grupo_2",
    id_grupo: 2,
    tema: "Festivais das Lanternas e Zodíaco",
    pergunta: "Qual animal mitológico e elemento representam o ciclo de 2024 na cultura tradicional chinesa, simbolizando vitalidade e progresso científico?",
    opcoes: ["Dragão de Madeira", "Serpente de Fogo", "Tigre de Terra", "Cão de Metal"],
    resposta_correta: "Dragão de Madeira",
    video_feedback_url: "https://www.youtube.com/embed/RkL6n327SIs",
    texto_explicativo: "Os anos na cultura chinesa seguem o calendário solar-lunar e associam animais do zodíaco a cinco elementos e polaridades Yin/Yang. 2024 é consagrado ao Dragão de Madeira, associado a novos começos e força renovadora."
  },
  {
    id: "grupo_3",
    id_grupo: 3,
    tema: "Instrumento Acústico Erhu",
    pergunta: "Qual instrumento chinês de duas cordas tocado com arco possui uma caixa sonora de madeira tradicionalmente encapada com pele de serpente?",
    opcoes: ["Erhu", "Pipa", "Guzheng", "Dizi"],
    resposta_correta: "Erhu",
    video_feedback_url: "https://www.youtube.com/embed/R6jYq2-z1c0",
    texto_explicativo: "O Erhu é um instrumento melódico milenar. Seu timbre expressivo deve-se às duas cordas sintonizadas em intervalos de quinta que vibram a caixa acústica recoberta de pele de cobra, produzindo ondas sonoras marcantes."
  },
  {
    id: "grupo_4",
    id_grupo: 4,
    tema: "Dança do Dragão na Feira",
    pergunta: "Durante as comemorações de rua no Brasil, como o Ano Novo Chinês em SP, a Dança do Dragão é conduzida pelos dançarinos seguindo qual elemento orbital?",
    opcoes: ["Uma pérola de sabedoria flutuante", "O mastro imperial", "O tambor da noite", "A bandeira da dinastia"],
    resposta_correta: "Uma pérola de sabedoria flutuante",
    video_feedback_url: "https://www.youtube.com/embed/nU2Wos-v680",
    texto_explicativo: "Na tradicional Dança do Dragão, os intérpretes sustentam o dragão sobre varas e correm imitando movimentos ondulados e serpenteantes, engajados na perseguição dramática de uma Esfera/Pérola que representa sabedoria e luz espiritual."
  },
  {
    id: "grupo_5",
    id_grupo: 5,
    tema: "Sabor das Feiras e Artes Marciais",
    pergunta: "Qual o famoso salgado brasileiro, muito popular nas feiras, que se originou da adaptação de pratos tradicionais chineses como o rolinho primavera?",
    opcoes: ["O Pastel de Feira", "A Coxinha de Frango", "O Pão de Queijo", "O Rissole de Presunto"],
    resposta_correta: "O Pastel de Feira",
    video_feedback_url: "https://www.youtube.com/embed/C5K-lWn_TjM",
    texto_explicativo: "Na metade do século XX, imigrantes chineses no Brasil adaptaram a massa fina e crocante do rolinho primavera (Spring Roll) para atender ao paladar brasileiro, originando o clássico pastel de feira recheado de carne e queijo de forma frita!"
  }
];

// Vídeos de premiação padrão
const defaultRewardVideos = [
  "https://www.youtube.com/embed/RkL6n327SIs", // Festival de Outono
  "https://www.youtube.com/embed/z12M1bI06iM", // Rota do Chá
  "https://www.youtube.com/embed/R6jYq2-z1c0", // Instrumentos
  "https://www.youtube.com/embed/nU2Wos-v680"  // Dança do Dragão
];

// Inicialização do localStorage em modo mock
const initMockDB = () => {
  if (!localStorage.getItem("mock_questions")) {
    localStorage.setItem("mock_questions", JSON.stringify(defaultQuestions));
  }
  if (!localStorage.getItem("mock_students_progress")) {
    localStorage.setItem("mock_students_progress", JSON.stringify([]));
  }
  if (!localStorage.getItem("mock_reward_videos")) {
    localStorage.setItem("mock_reward_videos", JSON.stringify(defaultRewardVideos));
  }
  if (!localStorage.getItem("mock_station_configs")) {
    localStorage.setItem("mock_station_configs", JSON.stringify(defaultStationConfigs));
  }
  if (!localStorage.getItem("mock_app_settings")) {
    localStorage.setItem("mock_app_settings", JSON.stringify(defaultAppSettings));
  }
};

if (isMockMode) {
  initMockDB();
}

/**
 * Busca todas as perguntas cadastradas.
 */
export async function getQuestions(): Promise<QuestionCard[]> {
  if (isMockMode) {
    const data = localStorage.getItem("mock_questions");
    return data ? JSON.parse(data) : defaultQuestions;
  } else {
    try {
      const qSnap = await getDocs(collection(db, "questions"));
      if (qSnap.empty) {
        return defaultQuestions;
      }
      const questions: QuestionCard[] = [];
      qSnap.forEach((doc) => {
        questions.push(doc.data() as QuestionCard);
      });
      return questions.sort((a, b) => a.id_grupo - b.id_grupo);
    } catch (error) {
      console.error("Erro ao carregar perguntas do Firestore, usando fallback padrão:", error);
      return defaultQuestions;
    }
  }
}

/**
 * Salva ou edita uma pergunta.
 */
export async function saveQuestion(question: QuestionCard): Promise<void> {
  if (isMockMode) {
    const data = localStorage.getItem("mock_questions");
    const questions: QuestionCard[] = data ? JSON.parse(data) : defaultQuestions;
    const index = questions.findIndex((q) => q.id === question.id);
    if (index > -1) {
      questions[index] = question;
    } else {
      questions.push(question);
    }
    localStorage.setItem("mock_questions", JSON.stringify(questions));
  } else {
    await setDoc(doc(db, "questions", question.id), question);
  }
}

/**
 * Salva o progresso e feedback do estudante.
 */
export async function saveStudentProgress(
  uid: string, 
  data: Partial<StudentProgress>
): Promise<void> {
  if (isMockMode) {
    const progressData = localStorage.getItem("mock_students_progress");
    const list: StudentProgress[] = progressData ? JSON.parse(progressData) : [];
    const index = list.findIndex((item) => item.uid === uid);
    
    if (index > -1) {
      list[index] = { ...list[index], ...data, lastActive: new Date().toISOString() };
    } else {
      const newUser: StudentProgress = {
        uid,
        name: data.name || "Aluno Desconhecido",
        email: data.email || "aluno@escola.mt.gov.br",
        score: data.score ?? 0,
        completed: data.completed ?? false,
        stars: data.stars ?? 0,
        feedbackText: data.feedbackText || "",
        lastActive: new Date().toISOString(),
        xp: data.xp ?? 0,
        badges: data.badges ?? []
      };
      list.push(newUser);
    }
    localStorage.setItem("mock_students_progress", JSON.stringify(list));
  } else {
    const userRef = doc(db, "users", uid);
    await setDoc(userRef, {
      ...data,
      lastActive: new Date().toISOString()
    }, { merge: true });
  }
}

/**
 * Retorna todos os alunos e seus progressos.
 */
export async function getStudentsProgress(): Promise<StudentProgress[]> {
  if (isMockMode) {
    const data = localStorage.getItem("mock_students_progress");
    return data ? JSON.parse(data) : [];
  } else {
    try {
      const qSnap = await getDocs(collection(db, "users"));
      const list: StudentProgress[] = [];
      qSnap.forEach((doc) => {
        const item = doc.data();
        list.push({
          uid: doc.id,
          name: item.name || "Aluno Desconhecido",
          email: item.email || "",
          score: item.score ?? 0,
          completed: item.completed ?? false,
          stars: item.stars ?? 0,
          feedbackText: item.feedbackText || "",
          lastActive: item.lastActive || new Date().toISOString(),
          xp: item.xp ?? 0,
          badges: item.badges ?? []
        });
      });
      return list;
    } catch (error) {
      console.error("Erro ao ler dados de progresso do Firestore:", error);
      return [];
    }
  }
}

/**
 * Carrega a lista dos 4 vídeos de premiação.
 */
export async function getRewardVideos(): Promise<string[]> {
  if (isMockMode) {
    const data = localStorage.getItem("mock_reward_videos");
    return data ? JSON.parse(data) : defaultRewardVideos;
  } else {
    try {
      const snap = await getDocs(collection(db, "reward_videos"));
      if (snap.empty) {
        return defaultRewardVideos;
      }
      const urls: string[] = [];
      snap.forEach((doc) => {
        urls.push(doc.data().url);
      });
      return urls;
    } catch (error) {
      console.error("Erro ao carregar vídeos de premiação:", error);
      return defaultRewardVideos;
    }
  }
}

/**
 * Salva a lista com os 4 vídeos de premiação.
 */
export async function saveRewardVideos(urls: string[]): Promise<void> {
  if (isMockMode) {
    localStorage.setItem("mock_reward_videos", JSON.stringify(urls));
  } else {
    for (let i = 0; i < urls.length; i++) {
      await setDoc(doc(db, "reward_videos", `video_${i + 1}`), { url: urls[i] });
    }
  }
}

/**
 * Busca todas as configurações de estações (imagens/videos de fundo e melodias).
 */
export async function getStationConfigs(): Promise<StationConfig[]> {
  if (isMockMode) {
    const data = localStorage.getItem("mock_station_configs");
    return data ? JSON.parse(data) : defaultStationConfigs;
  } else {
    try {
      const snap = await getDocs(collection(db, "station_configs"));
      if (snap.empty) {
        return defaultStationConfigs;
      }
      const configs: StationConfig[] = [];
      snap.forEach((doc) => {
        configs.push(doc.data() as StationConfig);
      });
      return configs.sort((a, b) => a.stationId - b.stationId);
    } catch (error) {
      console.error("Erro ao buscar configurações de estações:", error);
      return defaultStationConfigs;
    }
  }
}

/**
 * Grava ou edita a configuração de atmosfera de uma estação.
 */
export async function saveStationConfig(config: StationConfig): Promise<void> {
  if (isMockMode) {
    const data = localStorage.getItem("mock_station_configs");
    const configs: StationConfig[] = data ? JSON.parse(data) : defaultStationConfigs;
    const idx = configs.findIndex(c => c.stationId === config.stationId);
    if (idx > -1) {
      configs[idx] = config;
    } else {
      configs.push(config);
    }
    localStorage.setItem("mock_station_configs", JSON.stringify(configs));
  } else {
    await setDoc(doc(db, "station_configs", `station_${config.stationId}`), config);
  }
}

/**
 * Busca as configurações globais do aplicativo (como trilha sonora e volume).
 */
export async function getAppSettings(): Promise<AppSettings> {
  if (isMockMode) {
    const data = localStorage.getItem("mock_app_settings");
    return data ? JSON.parse(data) : defaultAppSettings;
  } else {
    try {
      const snap = await getDoc(doc(db, "app_settings", "global"));
      if (snap.exists()) {
        return snap.data() as AppSettings;
      } else {
        return defaultAppSettings;
      }
    } catch (error) {
      console.error("Erro ao buscar app_settings do Firestore:", error);
      return defaultAppSettings;
    }
  }
}

/**
 * Salva as configurações globais do aplicativo.
 */
export async function saveAppSettings(settings: AppSettings): Promise<void> {
  if (isMockMode) {
    localStorage.setItem("mock_app_settings", JSON.stringify(settings));
  } else {
    await setDoc(doc(db, "app_settings", "global"), settings);
  }
}
