import React from 'react';
import { Navigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { Compass } from 'lucide-react';

interface ProtectedRouteProps {
  children: React.ReactNode;
  requireAdmin?: boolean;
}

export const ProtectedRoute: React.FC<ProtectedRouteProps> = ({ children, requireAdmin = false }) => {
  const { user, loading } = useAuth();

  if (loading) {
    return (
      <div className="min-h-screen w-full bg-chinese-dark flex flex-col items-center justify-center gap-4">
        <Compass className="w-12 h-12 text-chinese-gold animate-spin-slow drop-shadow-[0_0_10px_rgba(255,215,0,0.4)]" />
        <p className="text-zinc-500 font-mono text-sm tracking-widest animate-pulse">CARREGANDO DADOS...</p>
      </div>
    );
  }

  if (!user) {
    // Redireciona para a Home se não estiver logado
    return <Navigate to="/" replace />;
  }

  if (requireAdmin && !user.isAdmin) {
    // Redireciona para a trilha se não for administrador
    return <Navigate to="/trilha" replace />;
  }

  return <>{children}</>;
};
