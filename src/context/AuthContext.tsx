import React, { createContext, useContext, useState, useEffect } from 'react';
import { onAuthStateChanged, signInWithPopup, signOut } from 'firebase/auth';
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
      // Escuta estado real do Firebase Auth
      const unsubscribe = onAuthStateChanged(auth, (firebaseUser) => {
        if (firebaseUser) {
          const userEmail = firebaseUser.email?.toLowerCase() || '';
          setUser({
            uid: firebaseUser.uid,
            displayName: firebaseUser.displayName,
            email: firebaseUser.email,
            isAdmin: userEmail.endsWith('@escola.mt.gov.br') || userEmail === 'professor@gmail.com' || userEmail === 'fernandesjohnnys@gmail.com'
          });
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

  const logout = async () => {
    setLoading(true);
    if (isMockMode) {
      localStorage.removeItem('mock_user');
      setUser(null);
    } else {
      await signOut(auth);
    }
    setLoading(false);
  };

  return (
    <AuthContext.Provider value={{ user, loading, isMock: isMockMode, loginWithGoogle, loginWithMock, logout }}>
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
