import React, { createContext, useContext, useEffect, useState } from 'react';
import { authApi, tokenStore } from './api';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [token, setToken] = useState(null);
  const [user, setUser] = useState(null);
  const [booting, setBooting] = useState(true);

  useEffect(() => {
    tokenStore.get().then((stored) => {
      setToken(stored);
      setBooting(false);
    });
  }, []);

  const persist = async (data) => {
    await tokenStore.set(data.token);
    setToken(data.token);
    setUser({ username: data.username, email: data.email });
  };

  const login = async (username, password) => {
    const { data } = await authApi.login(username, password);
    await persist(data);
  };

  const register = async (username, email, password) => {
    const { data } = await authApi.register(username, email, password);
    await persist(data);
  };

  const logout = async () => {
    await tokenStore.clear();
    setToken(null);
    setUser(null);
  };

  return (
    <AuthContext.Provider value={{ token, user, booting, login, register, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

export const useAuth = () => useContext(AuthContext);
