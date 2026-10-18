import React, { useCallback, useLayoutEffect, useState } from 'react';
import {
  ActivityIndicator,
  Alert,
  FlatList,
  Pressable,
  RefreshControl,
  StyleSheet,
  Text,
  View,
} from 'react-native';
import { useFocusEffect } from '@react-navigation/native';
import { errorMessage, taskApi } from '../api';
import { useAuth } from '../AuthContext';
import TaskCard from '../components/TaskCard';

const FILTERS = [
  { label: 'All', value: '' },
  { label: 'To Do', value: 'TODO' },
  { label: 'In Progress', value: 'IN_PROGRESS' },
  { label: 'Done', value: 'DONE' },
];

export default function TaskListScreen({ navigation }) {
  const { logout, user } = useAuth();
  const [tasks, setTasks] = useState([]);
  const [filter, setFilter] = useState('');
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);
  const [error, setError] = useState('');

  useLayoutEffect(() => {
    navigation.setOptions({
      headerRight: () => (
        <Pressable onPress={logout}>
          <Text style={styles.logout}>Logout{user ? ` (${user.username})` : ''}</Text>
        </Pressable>
      ),
    });
  }, [navigation, logout, user]);

  const load = useCallback(async () => {
    setError('');
    try {
      const params = { page: 0, size: 50, sort: 'createdAt,desc' };
      if (filter) params.status = filter;
      const { data } = await taskApi.list(params);
      setTasks(data.content);
    } catch (err) {
      setError(errorMessage(err, 'Failed to load tasks.'));
    } finally {
      setLoading(false);
      setRefreshing(false);
    }
  }, [filter]);

  // Reload whenever the screen regains focus (e.g. after saving in the form).
  useFocusEffect(
    useCallback(() => {
      setLoading(true);
      load();
    }, [load])
  );

  const confirmDelete = (task) => {
    Alert.alert('Delete task', `Delete "${task.title}"?`, [
      { text: 'Cancel', style: 'cancel' },
      {
        text: 'Delete',
        style: 'destructive',
        onPress: async () => {
          try {
            await taskApi.remove(task.id);
            load();
          } catch (err) {
            setError(errorMessage(err, 'Could not delete the task.'));
          }
        },
      },
    ]);
  };

  return (
    <View style={styles.container}>
      <View style={styles.filters}>
        {FILTERS.map((f) => (
          <Pressable
            key={f.value}
            style={[styles.chip, filter === f.value && styles.chipActive]}
            onPress={() => setFilter(f.value)}
          >
            <Text style={[styles.chipText, filter === f.value && styles.chipTextActive]}>
              {f.label}
            </Text>
          </Pressable>
        ))}
      </View>

      {!!error && <Text style={styles.error}>{error}</Text>}

      {loading ? (
        <ActivityIndicator style={{ marginTop: 40 }} size="large" color="#4361ee" />
      ) : (
        <FlatList
          data={tasks}
          keyExtractor={(t) => String(t.id)}
          contentContainerStyle={{ padding: 16 }}
          refreshControl={
            <RefreshControl
              refreshing={refreshing}
              onRefresh={() => {
                setRefreshing(true);
                load();
              }}
            />
          }
          ListEmptyComponent={<Text style={styles.empty}>No tasks yet. Tap + to add one.</Text>}
          renderItem={({ item }) => (
            <TaskCard
              task={item}
              onEdit={(t) => navigation.navigate('TaskForm', { task: t })}
              onDelete={confirmDelete}
            />
          )}
        />
      )}

      <Pressable style={styles.fab} onPress={() => navigation.navigate('TaskForm')}>
        <Text style={styles.fabText}>+</Text>
      </Pressable>
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#f4f6f8' },
  filters: { flexDirection: 'row', padding: 12, flexWrap: 'wrap' },
  chip: {
    paddingHorizontal: 14,
    paddingVertical: 7,
    borderRadius: 20,
    backgroundColor: '#e9ecef',
    marginRight: 8,
    marginBottom: 4,
  },
  chipActive: { backgroundColor: '#4361ee' },
  chipText: { fontWeight: '600', color: '#495057', fontSize: 13 },
  chipTextActive: { color: '#fff' },
  empty: { textAlign: 'center', color: '#888', marginTop: 40 },
  error: {
    color: '#e63946',
    backgroundColor: '#fdeaec',
    padding: 10,
    marginHorizontal: 16,
    borderRadius: 6,
  },
  logout: { color: '#4361ee', fontWeight: '600' },
  fab: {
    position: 'absolute',
    right: 20,
    bottom: 24,
    width: 56,
    height: 56,
    borderRadius: 28,
    backgroundColor: '#4361ee',
    alignItems: 'center',
    justifyContent: 'center',
    elevation: 4,
  },
  fabText: { color: '#fff', fontSize: 30, marginTop: -2 },
});
