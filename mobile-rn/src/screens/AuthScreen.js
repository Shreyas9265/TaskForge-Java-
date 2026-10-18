import React, { useState } from 'react';
import {
  ActivityIndicator,
  KeyboardAvoidingView,
  Platform,
  Pressable,
  StyleSheet,
  Text,
  TextInput,
  View,
} from 'react-native';
import { useAuth } from '../AuthContext';
import { errorMessage } from '../api';

export default function AuthScreen() {
  const { login, register } = useAuth();
  const [mode, setMode] = useState('login');
  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const isLogin = mode === 'login';

  const submit = async () => {
    setError('');
    setLoading(true);
    try {
      if (isLogin) await login(username.trim(), password);
      else await register(username.trim(), email.trim(), password);
    } catch (err) {
      setError(errorMessage(err, isLogin ? 'Login failed.' : 'Registration failed.'));
    } finally {
      setLoading(false);
    }
  };

  return (
    <KeyboardAvoidingView
      style={styles.container}
      behavior={Platform.OS === 'ios' ? 'padding' : undefined}
    >
      <Text style={styles.brand}>TaskForge</Text>
      <View style={styles.card}>
        <Text style={styles.title}>{isLogin ? 'Log in' : 'Create an account'}</Text>
        {!!error && <Text style={styles.error}>{error}</Text>}

        <TextInput
          style={styles.input}
          placeholder="Username"
          autoCapitalize="none"
          value={username}
          onChangeText={setUsername}
        />
        {!isLogin && (
          <TextInput
            style={styles.input}
            placeholder="Email"
            autoCapitalize="none"
            keyboardType="email-address"
            value={email}
            onChangeText={setEmail}
          />
        )}
        <TextInput
          style={styles.input}
          placeholder="Password (min 6 characters)"
          secureTextEntry
          value={password}
          onChangeText={setPassword}
        />

        <Pressable style={styles.button} onPress={submit} disabled={loading}>
          {loading ? (
            <ActivityIndicator color="#fff" />
          ) : (
            <Text style={styles.buttonText}>{isLogin ? 'Log in' : 'Register'}</Text>
          )}
        </Pressable>

        <Pressable onPress={() => setMode(isLogin ? 'register' : 'login')}>
          <Text style={styles.switch}>
            {isLogin ? "Don't have an account? Register" : 'Already have an account? Log in'}
          </Text>
        </Pressable>
      </View>
    </KeyboardAvoidingView>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#1a1a2e', justifyContent: 'center', padding: 20 },
  brand: { color: '#fff', fontSize: 32, fontWeight: '800', textAlign: 'center', marginBottom: 24 },
  card: { backgroundColor: '#fff', borderRadius: 12, padding: 20 },
  title: { fontSize: 20, fontWeight: '700', marginBottom: 16 },
  input: {
    borderWidth: 1,
    borderColor: '#ccc',
    borderRadius: 8,
    padding: 12,
    marginBottom: 12,
    fontSize: 15,
  },
  button: { backgroundColor: '#4361ee', borderRadius: 8, padding: 14, alignItems: 'center' },
  buttonText: { color: '#fff', fontWeight: '700', fontSize: 15 },
  switch: { color: '#4361ee', textAlign: 'center', marginTop: 16 },
  error: {
    color: '#e63946',
    backgroundColor: '#fdeaec',
    padding: 10,
    borderRadius: 6,
    marginBottom: 12,
  },
});
