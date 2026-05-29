import React, { createContext, useContext, useState, useEffect } from 'react';
import { onAuthStateChanged, signInWithPopup, signOut, signInAnonymously } from 'firebase/auth';
import { auth, provider, isMockMode } from '../lib/firebase';

interface AuthUser {
  uid: string;
  displayName: string | null;
  email: string | null;
  isAdmin?: boolean;
}

interface AuthContextType {
  user: AuthUser | null;
  loading: boolean;
  isMock: boolean;
  loginWithGoogle: () => Promise<void>;
  loginWithMock: (name: string, email: string) => Promise<void>;
  loginAnonymously: (name: string, email: string) => Promise<void>;
  logout: () => Promise<void>;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<AuthUser | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (isMockMode) {
      // Carrega usuário salvo do localStorage no modo Mock
      const savedUser = localStorage.getItem('mock_user');
      if (savedUser) {
        setUser(JSON.parse(savedUser));
      }
      setLoading(false);
    } else {
      // Verifica primeiro se há um fallback_user salvo localmente
      const savedFallback = localStorage.getItem('fallback_user');
      if (savedFallback) {
        setUser(JSON.parse(savedFallback));
        setLoading(false);
        return;
      }

      // Escuta estado real do Firebase Auth
      const unsubscribe = onAuthStateChanged(auth, (firebaseUser) => {
        if (firebaseUser) {
          if (firebaseUser.isAnonymous) {
            // Usuário anônimo/Acesso rápido
            const savedName = localStorage.getItem('anon_name') || 'Aluno Convidado';
            const savedEmail = localStorage.getItem('anon_email') || 'aluno@escola.mt.gov.br';
            setUser({
              uid: firebaseUser.uid,
              displayName: savedName,
              email: savedEmail,
              isAdmin: false // NUNCA permitir admin para acessos rápidos/anônimos por segurança
            });
          } else {
            // Login via Google autenticado
            const userEmail = firebaseUser.email?.toLowerCase() || '';
            setUser({
              uid: firebaseUser.uid,
              displayName: firebaseUser.displayName,
              email: firebaseUser.email,
              isAdmin: userEmail.endsWith('@escola.mt.gov.br') || userEmail === 'professor@gmail.com' || userEmail === 'fernandesjohnnys@gmail.com'
            });
          }
        } else {
          setUser(null);
        }
        setLoading(false);
      });
      return unsubscribe;
    }
  }, []);

  const loginWithGoogle = async () => {
    if (isMockMode) {
      throw new Error("Modo Mock ativado. Use loginWithMock para entrar.");
    }
    // Remove dados anteriores de acesso rápido se houver
    localStorage.removeItem('anon_name');
    localStorage.removeItem('anon_email');
    localStorage.removeItem('fallback_user');
    await signInWithPopup(auth, provider);
  };

  const loginWithMock = async (name: string, email: string) => {
    setLoading(true);
    // Simula uma resposta de rede
    await new Promise((resolve) => setTimeout(resolve, 800));
    const mockUser: AuthUser = {
      uid: `mock-${Date.now()}`,
      displayName: name || 'Aluno Convidado',
      email: email || 'aluno@escola.mt.gov.br',
      isAdmin: email.toLowerCase().includes('admin') || email.toLowerCase().includes('professor') || email.toLowerCase() === 'fernandesjohnnys@gmail.com'
    };
    setUser(mockUser);
    localStorage.setItem('mock_user', JSON.stringify(mockUser));
    
    // Registrar na lista de alunos simulada do localStorage
    const savedProgress = localStorage.getItem('mock_students_progress');
    const progressList = savedProgress ? JSON.parse(savedProgress) : [];
    
    // Adiciona se não existir
    if (!progressList.some((p: any) => p.email === mockUser.email)) {
      progressList.push({
        uid: mockUser.uid,
        name: mockUser.displayName,
        email: mockUser.email,
        score: 0,
        completed: false,
        stars: 0,
        lastActive: new Date().toISOString(),
        xp: 0,
        badges: []
      });
      localStorage.setItem('mock_students_progress', JSON.stringify(progressList));
    }
    
    setLoading(false);
  };

  const loginAnonymously = async (name: string, email: string) => {
    if (isMockMode) {
      await loginWithMock(name, email);
      return;
    }
    setLoading(true);
    try {
      localStorage.setItem('anon_name', name);
      localStorage.setItem('anon_email', email);
      const credential = await signInAnonymously(auth);
      const anonUser = credential.user;
      setUser({
        uid: anonUser.uid,
        displayName: name || 'Aluno Convidado',
        email: email || 'aluno@escola.mt.gov.br',
        isAdmin: false // Segurança: Proibido acesso administrativo a logins anônimos
      });
    } catch (error) {
      console.error("Erro ao entrar anonimamente:", error);
      // Fallback local se o provedor anônimo estiver desativado no Firebase console
      const fallbackUid = `anon-${Date.now()}`;
      const fallbackUser = {
        uid: fallbackUid,
        displayName: name || 'Aluno Convidado',
        email: email || 'aluno@escola.mt.gov.br',
        isAdmin: false
      };
      localStorage.setItem('anon_name', name);
      localStorage.setItem('anon_email', email);
      setUser(fallbackUser);
      localStorage.setItem('fallback_user', JSON.stringify(fallbackUser));
    } finally {
      setLoading(false);
    }
  };

  const logout = async () => {
    setLoading(true);
    localStorage.removeItem('anon_name');
    localStorage.removeItem('anon_email');
    localStorage.removeItem('fallback_user');
    if (isMockMode) {
      localStorage.removeItem('mock_user');
      setUser(null);
    } else {
      await signOut(auth);
      setUser(null);
    }
    setLoading(false);
  };

  return (
    <AuthContext.Provider value={{ user, loading, isMock: isMockMode, loginWithGoogle, loginWithMock, loginAnonymously, logout }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth deve ser usado dentro de um AuthProvider');
  }
  return context;
};
