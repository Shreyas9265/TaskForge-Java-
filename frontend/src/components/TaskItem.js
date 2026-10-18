import React from 'react';

export default function TaskItem({ task, onEdit, onDelete }) {
  return (
    <div className="task-item">
      <div>
        <div style={{ fontWeight: 600, marginBottom: 4 }}>{task.title}</div>
        {task.description && (
          <div style={{ fontSize: 13, color: '#555', marginBottom: 6 }}>{task.description}</div>
        )}
        <span className={`badge badge-${task.status.toLowerCase()}`}>{task.status.replace('_', ' ')}</span>
        <span className={`badge badge-${task.priority.toLowerCase()}`}>{task.priority}</span>
        {task.dueDate && (
          <span style={{ fontSize: 12, color: '#888', marginLeft: 6 }}>Due {task.dueDate}</span>
        )}
      </div>
      <div className="task-actions">
        <button className="btn btn-secondary" onClick={() => onEdit(task)}>Edit</button>
        <button className="btn btn-danger" onClick={() => onDelete(task.id)}>Delete</button>
      </div>
    </div>
  );
}
