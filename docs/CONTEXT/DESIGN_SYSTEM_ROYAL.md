# Design System: Royal Strategy & Ghost Aesthetic

Este documento define as regras visuais, paleta de cores, tipografia e diretrizes de design para o Web App da **Feira das Nações: Experiência Sino-Brasileira**.

---

## 1. Princípios de Design
- **Imersão Cultural**: O design deve evocar a atmosfera da China Imperial tradicional, combinada com a modernidade da presença chinesa no Brasil.
- **Estética Royal & Ghost**:
  - **Tons Vermelhos Imperiais (Royal Chinese Red)**: Simbolizam sorte, alegria e celebração.
  - **Tons Ouro e Amarelo Imperial (Royal Gold/Yellow)**: Representam nobreza, terra e a sabedoria do Imperador.
  - **Tons Jade (Emerald/Green)**: Usados para elementos saudáveis, harmônicos, e capítulos específicos (como o Ritual do Chá e a Fritura do Pastel).
  - **Ghost Aesthetic**: Efeitos de glassmorphism translúcidos sobrepostos a planos de fundo dinâmicos (vídeos de apresentações dos alunos ou paisagens chinesas tradicionais).
- **Mobile-First & Responsivo**: Interface extremamente fluida no celular (acesso rápido via QR Code na feira) e perfeitamente escalável no desktop.

---

## 2. Paleta de Cores Semânticas (Tailwind CSS)

> [!WARNING]
> É estritamente proibido o uso de códigos hexadecimais soltos (`#HEX`) no código dos componentes. Utilize sempre classes utilitárias semânticas do Tailwind.

| Elemento / Estado | Classes Tailwind Sugeridas | Descrição |
| :--- | :--- | :--- |
| **Fundo Principal (Escuro/Imperial)** | `bg-zinc-950`, `bg-red-995/95`, `bg-emerald-995/95` | Tons escuros profundos com overlay de vermelho ou jade. |
| **Bordas Imperiais** | `border-yellow-600/30`, `border-yellow-500/50` | Efeito de moldura imperial chinesa tradicional. |
| **Textos Principais** | `text-zinc-100`, `text-zinc-200` | Excelente legibilidade sobre fundos escuros. |
| **Destaques & Ouro** | `text-chinese-gold`, `text-yellow-400`, `text-yellow-450` | Para títulos importantes, conquistas, XP e botões principais. |
| **Ações Primárias / Ganhos** | `bg-gradient-to-r from-chinese-gold to-yellow-600` | Botões de prosseguir, enviar, concluir. |
| **Ações Secundárias / Alertas**| `bg-gradient-to-r from-chinese-red to-red-700` | Botões de fechar, resetar ou alertas visuais. |
| **Sucesso / Concluído** | `border-emerald-500`, `text-emerald-450`, `bg-green-950/40` | Sinalizadores de capítulos concluídos ou acertos de mini-games. |

---

## 3. Micro-animações e Elementos Visuais
- **Lanternas Flutuantes (`.lantern-particle`)**: Loop contínuo de lanternas de papel subindo suavemente no fundo da tela da trilha.
- **Dragão Imperial (`.dragon-anim`)**: Animação comemorativa do dragão chinês que cruza a tela quando o aluno vence um desafio ou conclui a jornada.
- **Vídeo de Fundo da Home**: Execução em loop do vídeo tradicional `/video/festival_lanternas.mp4` a 25% de opacidade com gradiente escuro, proporcionando imersão imediata desde a tela de login.
- **Transição de Capítulos**: Efeito de fade-in e scale nas trocas de cena.
- **Áudio Imersivo de Fundo (Exclusivo na Home)**: O áudio tradicional chinês em WebM (`/audio/traditional_chinese_music.webm`) executa em loop no volume de 0.25 **exclusivamente na tela inicial e de login** (Home.tsx). Ao navegar para a Trilha do Quiz ou para o Painel Admin, a música de fundo é interrompida para evitar sobreposição ou distração do público e permitir que os alunos comandem a ordem unida ao vivo.
- **Mute Local de Efeitos**: O botão de som na Trilha passa a atuar apenas silenciando localmente os efeitos sonoros de interação (sinos e transições de gongo), sem disparar áudio de fundo em loop.

---

## 5. Recompensa Final: Carta Mítica da Sabedoria
- **Mecanismo de Geração**: Canvas do HTML5 operando de forma 100% offline e rápida. O card compõe a foto capturada (câmera ou upload) recortada em anel dourado central, o nome e signo do participante, sua pontuação em XP, provérbios tradicionais confucionistas sobre ser um eterno aprendiz guiado por seus professores, molduras douradas e a identidade da escola:
  * *Escola*: `EE CM Cremilda de Oliveira - 2026`
  * *Evento*: `Feira das Nações - Cultura Chinesa no Brasil - 7º D`
  * *Professor*: `Prof. Johnny Fernandes`
- **Seleção Dinâmica de Fundos**: O participante pode selecionar entre 5 atmosferas visuais carregadas dinamicamente como plano de fundo do Canvas:
  * **Dragões & Fênix** (`/images/dragoes_fenix.png`)
  * **Energia Explosiva** (`/images/explosiva.png`)
  * **Cultura do Chá** (`/images/cultura_cha.png`)
  * **Instrumentos Tradicionais** (`/images/instrumentos.png`)
  * **Festival das Lanternas** (`/images/festival.png`)
- **Variedade de Sabedoria (30 Provérbios)**: Ao gerar a foto, o sistema sorteia aleatoriamente um de 30 provérbios confucionistas e taoístas clássicos selecionados com foco em elevação, heroísmo silencioso, respeito mútuo e perseverança escolar. O texto é quebrado de forma inteligente (`splitText`) para ajuste perfeito na caixa de pergaminho.
- **Limite de Tentativas**: O participante tem direito a um limite estrito de **3 tentativas** para capturar ou carregar fotos. A escolha de imagens de fundo é ilimitada e não consome tentativas. Atingido o limite de fotos, o app bloqueia novas capturas e consagra o card final para download imediato.

---

## 4. O Papel de Mentora da Mei-Ling
- **Avatar Fotorrealista**: Localizado em `/public/mei_ling_avatar.png`, projetado com bordas duplas (`chinese-border-double`) e container translúcido glassmorphism (`bg-red-950/80 backdrop-blur-md`).
- **Missão Filosófica**: Mei-Ling atua como o arquétipo da Mentora na *Jornada do Herói*. Em vez de apenas apresentar dados ou cobrar testes estéreis, ela utiliza a história da migração chinesa, o rigor do Kung Fu/Wushu e a harmonia acústica para provocar nos estudantes do 7º ano uma reflexão sobre a superação de seus próprios dragões internos (como distração digital excessiva e pressões precoces).
- **Conteúdo de Diálogos**: Os textos de boas-vindas, as introduções das estações e a aclamação final estimulam a paz, a escuta atenta, o trabalho harmônico em grupo e o foco disciplinado, ressignificando conquistas de XP como vitórias de caráter pessoal. A narrativa de fundamentação completa encontra-se documentada no arquivo de contexto [NARRATIVA_HEROI.md](file:///c:/Projetos/feira-nacoes/docs/CONTEXT/NARRATIVA_HEROI.md).
