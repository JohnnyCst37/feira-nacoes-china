# ChessHub Pro / Feira das Nações

## Contexto Geral
Este projeto é um Web App mobile-first desenvolvido para a Feira das Nações de uma escola no Mato Grosso (MT). O objetivo é guiar usuários em uma trilha interativa sobre a Cultura Chinesa no Brasil, apresentando trabalhos de alunos em vídeo e testando conhecimentos via Quiz.

**Missão Pedagógica (A Jornada do Herói)**:
Os alunos do 7º ano estão expostos a excessos e influências do mundo adulto digital. Por isso, a personagem **Mei-Ling** atua não apenas como uma narradora de dados, mas como uma mentora que os conecta a uma jornada de heroísmo. O objetivo é que, através do conhecimento histórico, do rigor das artes tradicionais (Kung Fu, Danças, Caligrafia) e do alinhamento musical, eles percebam suas próprias lutas internas e se inspirem a vencê-las com persistência, disciplina, orgulho e vontade de vencer.

**Persona Alvo**: O Professor Empreendedor (foco em educação, facilidade de uso, engajamento e valor pedagógico profundo para escolas públicas do MT).
**Privacidade e Segurança (LGPD)**: Foco rigoroso em segurança, especialmente ao lidar com dados de menores e autenticação.

---

## Stack Tecnológica (Opção A)
- **Framework/Bundler**: Vite
- **Biblioteca principal**: React 19
- **Linguagem**: TypeScript (Strict Mode)
- **Estilização**: Tailwind CSS (estilo customizado baseado no Design System Royal Strategy / Ghost Aesthetic)
- **Ícones**: Lucide React
- **Banco de Dados / Autenticação**: Firebase (Auth + Firestore)

---

## Estrutura do Repositório
- `docs/`: Documentação geral do projeto.
  - `docs/MANAGEMENT/PROGRESS_LOG.md`: Acompanhamento de progresso de tarefas.
- `src/`: Código fonte do projeto.
  - `src/lib/`: Configurações e integrações (ex: Firebase).
  - `src/pages/`: Páginas da aplicação.
  - `src/components/`: Componentes reutilizáveis.
- `public/`: Arquivos estáticos.
- `package.json`, `tsconfig.json`, `vite.config.ts`: Configurações do ambiente de desenvolvimento.

---

## Decisões Arquiteturais e de Design
- **Autenticação**: Google OAuth via Firebase Auth.
- **Armazenamento**: Respostas e progresso dos alunos salvos no Firestore.
- **Design System (Royal Strategy / Ghost Aesthetic)**: Uso estrito de cores semânticas e componentes premium. Evitar cores hexadecimais soltas; utilizar Tailwind com micro-animações, gradientes elegantes e suporte a dark mode.

---

## Estado Atual e Ajustes de Design da Carta Mítica
1. **Design de Contraste e Legibilidade**:
   - A Carta Mítica da Sabedoria foi redesenhada no Canvas. O provérbio agora é exibido sobre um papiro bege tradicional (`#fbf5e2`) com borda marrom dupla e fonte marrom escura (`#3d2314`), eliminando o problema do fundo preto translúcido apagado.
   - Foram adicionadas barras sólidas em Vermelho Imperial Escuro (`#4a0707`) com bordas douradas no topo e na base do card para acomodar as informações institucionais com contraste de altíssimo nível.
2. **Logos da Escola Integradas**:
   - A barra superior agora desenha os logotipos `LOGO_COV.png` e `LOGO_CIVICO_COV.png` nas laterais do título.
   - A barra inferior exibe o logotipo horizontal horizontal da SEDUC MT `SOMOSTODOSEDU.png` ao lado da identificação da turma (7º Ano D) e do Prof. Johnny Fernandes.
3. **Resolução de Bugs & Novas Opções**:
   - Corrigido o erro de sintaxe JSX `'return' outside of function` em `src/pages/Trilha.tsx` eliminando uma chave extra (`};`) que fechava o escopo do componente antes do tempo. O comando `npm run build` agora compila para produção com 100% de sucesso.
   - Implementada a opção **"Gerar sem Foto"** no gerador. Se o aluno escolher essa opção, o Canvas do card ignora a selfie e exibe o plano de fundo original intacto (mantendo os guerreiros tradicionais com seus rostos originais ou o céu central nos cenários de medalhão).
   - Ocultei dinamicamente os controles deslizantes de fusão de rosto (Y-offset e Zoom) na UI caso o card seja gerado sem foto, deixando a interface limpa.
4. **Resiliência e Preview de Vídeos (YouTube/Drive)**:
   - Adicionada área de pré-visualização (Iframe) em tempo real no Painel Admin para o professor testar se a incorporação dos vídeos funciona ou se é bloqueada pelo YouTube.
   - Adicionados links de escape amigáveis (*"Não carregou? Assistir diretamente no YouTube/Drive ↗"*) abaixo de todos os players de vídeo na Trilha e no Admin para mitigar erros de indisponibilidade de embed.
   - Atualizada a Estação 3 (Instrumentos) com o vídeo padrão do Erhu fornecido pelo professor (`https://www.youtube.com/embed/1XaAreqeiI0`).
