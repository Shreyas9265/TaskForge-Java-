import React, { useState } from 'react';
import {
  ActivityIndicator,
  Pressable,
  ScrollView,
  StyleSheet,
  Text,
  TextInput,
  View,
} from 'react-native';
import { errorMessage, taskApi } from '../api';

const STATUSES = ['TODO', 'IN_PROGRESS', 'DONE'];
const PRIORITIES = ['LOW', 'MEDIUM', 'HIGH'];

function Segmented({ options, value, onChange }) {
  return (
    <View style={styles.segment}>
      {options.map((opt) => (
        <Pressable
          key={opt}
          style={[styles.segmentItem, value === opt && styles.segmentActive]}
          onPress={() => onChange(opt)}
        >
          <Text style={[styles.segmentText, value === opt && styles.segmentTextActive]}>
            {opt.replace('_', ' ')}
          </Text>
        </Pressable>
      ))}
    </View>
  );
}

export default function TaskFormScreen({ route, navigation }) {
  const existing = route.params?.task;
  const [title, setTitle] = useState(existing?.title ?? '');
  const [description, setDescription] = useState(existing?.description ?? '');
  const [status, setStatus] = useState(existing?.status ?? 'TODO');
  const [priority, setPriority] = useState(existing?.priority ?? 'MEDIUM');
  const [dueDate, setDueDate] = useState(existing?.dueDate ?? '');
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');

  const save = async () => {
    if (!title.trim()) {
      setError('Title is required.');
      return;
    }
    if (dueDate && !/^\d{4}-\d{2}-\d{2}$/.test(dueDate)) {
      setError('Due date must look like 2026-12-31.');
      return;
    }
    setSaving(true);
    setError('');
    const payload = {
      title: title.trim(),
      description: description.trim(),
      status,
      priority,
      dueDate: dueDate || null,
    };
    try {
      if (existing) await taskApi.update(existing.id, payload);
      else await taskApi.create(payload);
      navigation.goBack();
    } catch (err) {
      setError(errorMessage(err, 'Could not save the task.'));
    } finally {
      setSaving(false);
    }
  };

  return (
    <ScrollView style={styles.container} contentContainerStyle={{ padding: 16 }}>
      {!!error && <Text style={styles.error}>{error}</Text>}

      <Text style={styles.label}>Title</Text>
      <TextInput style={styles.input} value={title} onChangeText={setTitle} />

      <Text style={styles.label}>Description</Text>
      <TextInput
        style={[styles.input, { height: 90, textAlignVertical: 'top' }]}
        multiline
        value={description}
        onChangeText={setDescription}
      />

      <Text style={styles.label}>Status</Text>
      <Segmented options={STATUSES} value={status} onChange={setStatus} />

      <Text style={styles.label}>Priority</Text>
      <Segmented options={PRIORITIES} value={priority} onChange={setPriority} />

      <Text style={styles.label}>Due date (YYYY-MM-DD, optional)</Text>
      <TextInput
        style={styles.input}
        value={dueDate}
        onChangeText={setDueDate}
        placeholder="2026-12-31"
        autoCapitalize="none"
      />

      <Pressable style={styles.button} onPress={save} disabled={saving}>
        {saving ? <ActivityIndicator color="#fff" /> : <Text style={styles.buttonText}>Save</Text>}
      </Pressable>
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#f4f6f8' },
  label: { fontWeight: '600', marginBottom: 6, marginTop: 8 },
  input: {
    backgroundColor: '#fff',
    borderWidth: 1,
    borderColor: '#ccc',
    borderRadius: 8,
    padding: 12,
    fontSize: 15,
  },
  segment: { flexDirection: 'row', backgroundColor: '#e9ecef', borderRadius: 8, padding: 3 },
  segmentItem: { flex: 1, paddingVertical: 8, alignItems: 'center', borderRadius: 6 },
  segmentActive: { backgroundColor: '#4361ee' },
  segmentText: { fontSize: 12, fontWeight: '600', color: '#495057' },
  segmentTextActive: { color: '#fff' },
  button: {
    backgroundColor: '#4361ee',
    borderRadius: 8,
    padding: 14,
    alignItems: 'center',
    marginTop: 24,
  },
  buttonText: { color: '#fff', fontWeight: '700', fontSize: 15 },
  error: {
    color: '#e63946',
    backgroundColor: '#fdeaec',
    padding: 10,
    borderRadius: 6,
    marginBottom: 8,
  },
});
