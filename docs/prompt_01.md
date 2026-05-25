Você é um Guia Virtual sábio e carismático da Feira das Nações. Seu objetivo é guiar os usuários por uma jornada sobre a Cultura Chinesa no Brasil.
Você deve se comunicar em parágrafos curtos (ideais para balões de texto em um celular).
Suas funções:

Receber o nome do usuário e chamá-lo pelo nome.

Quando o usuário acertar ou errar uma pergunta sobre a China (ex: feiras, comidas, lutas), você deve dar um breve feedback cultural antes de chamar o vídeo explicativo do aluno.

Gerar um provérbio chinês aleatório relacionado a esforço, sabedoria ou natureza sempre que o usuário pedir ou ao finalizar a trilha.

Destacar sempre o quanto a cultura chinesa está presente no dia a dia do brasileiro (ex: pastel de feira, acupuntura, artes marciais).
Tom de voz: Acolhedor, misterioso, divertido e com pitadas de filosofia oriental.

2. O Frontend e o Jogo (Gerando o código sem esforço)
Como o objetivo é não perder tempo com infraestrutura, você pode usar uma IA assistente de código (como ChatGPT ou Cursor) para gerar a base do aplicativo Next.js integrado ao Firebase.

Use este prompt no seu assistente de código para gerar a estrutura pronta:

Crie a estrutura de um Web App mobile-first usando Next.js, Tailwind CSS e Firebase.
Requisitos da Interface:

Splash Screen: Ao carregar, mostre uma tela escura com o "Festival das Lanternas". Após 5 segundos, mude para "Ano Novo Chinês 2024" com uma animação em CSS de um Dragão cruzando a tela. Em seguida, libere o botão de Login.

Autenticação: Login obrigatório com Google via Firebase Auth. Salve o nome do usuário no Firestore para eu contar os acessos.

Dashboard do Professor: Uma rota /admin (protegida) onde eu possa ver o total de usuários logados, a pontuação de cada um e fazer a ingestão dos trabalhos dos alunos (cadastrar Título, Link do Vídeo, Pergunta e Resposta Correta no Firestore).

Trilha do Usuário: O usuário vê um balão de texto (consumindo a API do Google Gemini) de um Mestre Chinês. Abaixo, a pergunta do Quiz gerada a partir do Firestore. Se ele responder, um Modal de Vídeo se abre exibindo o trabalho do aluno correspondente como feedback (certo ou errado).

Finalização: Uma tela de agradecimento com uma nota de experiência (estrelas) que é salva no banco, e uma mensagem final gerada pela API.
Me dê o código dos componentes principais e a configuração do Firebase.

3. Ingestão dos Vídeos dos Alunos
Para facilitar a sua vida no gerenciamento dos 5 grupos da turma, você não precisa de um painel complexo no início. Você pode cadastrar os trabalhos diretamente no banco de dados (Firestore) com a seguinte estrutura de "Cards":

id_grupo: (ex: 1)

tema: "Imigração e Comércio"

pergunta: "Qual alimento tradicional das feiras brasileiras teve forte influência dos imigrantes chineses?"

opcoes: ["Acarajé", "Pastel", "Pão de Queijo", "Tapioca"]

resposta_correta: "Pastel"

video_feedback_url: "link_do_video_do_aluno_no_youtube_ou_drive"

Quando o usuário clica na opção no celular, o Next.js compara a resposta. O AI Studio solta uma frase de efeito ("O caminho da sabedoria começa na feira!"), e o vídeo do aluno entra em cena explicando a história do pastel.