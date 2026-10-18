import React from 'react';
import { Pressable, StyleSheet, Text, View } from 'react-native';

const STATUS_COLORS = {
  TODO: { bg: '#ffe8cc', fg: '#d9730d' },
  IN_PROGRESS: { bg: '#d0ebff', fg: '#1971c2' },
  DONE: { bg: '#d3f9d8', fg: '#2b8a3e' },
};

const PRIORITY_COLORS = {
  LOW: { bg: '#e9ecef', fg: '#495057' },
  MEDIUM: { bg: '#fff3bf', fg: '#e67700' },
  HIGH: { bg: '#ffe3e3', fg: '#c92a2a' },
};

function Badge({ label, colors }) {
  return (
    <View style={[styles.badge, { backgroundColor: colors.bg }]}>
      <Text style={[styles.badgeText, { color: colors.fg }]}>{label.replace('_', ' ')}</Text>
    </View>
  );
}

export default function TaskCard({ task, onEdit, onDelete }) {
  return (
    <View style={styles.card}>
      <Text style={styles.title}>{task.title}</Text>
      {!!task.description && <Text style={styles.desc}>{task.description}</Text>}
      <View style={styles.row}>
        <Badge label={task.status} colors={STATUS_COLORS[task.status]} />
        <Badge label={task.priority} colors={PRIORITY_COLORS[task.priority]} />
        {!!task.dueDate && <Text style={styles.due}>Due {task.dueDate}</Text>}
      </View>
      <View style={styles.actions}>
        <Pressable onPress={() => onEdit(task)}>
          <Text style={styles.edit}>Edit</Text>
        </Pressable>
        <Pressable onPress={() => onDelete(task)}>
          <Text style={styles.delete}>Delete</Text>
        </Pressable>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  card: { backgroundColor: '#fff', borderRadius: 10, padding: 16, marginBottom: 12 },
  title: { fontSize: 16, fontWeight: '700', marginBottom: 4 },
  desc: { color: '#555', marginBottom: 8 },
  row: { flexDirection: 'row', alignItems: 'center', flexWrap: 'wrap' },
  badge: { borderRadius: 20, paddingHorizontal: 10, paddingVertical: 3, marginRight: 6 },
  badgeText: { fontSize: 11, fontWeight: '700' },
  due: { fontSize: 12, color: '#888' },
  actions: { flexDirection: 'row', justifyContent: 'flex-end', marginTop: 10 },
  edit: { color: '#4361ee', fontWeight: '600', marginRight: 20 },
  delete: { color: '#e63946', fontWeight: '600' },
});
