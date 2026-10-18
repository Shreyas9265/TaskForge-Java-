import React, { useCallback, useEffect, useState } from 'react';
import { taskApi } from '../services/api';
import TaskForm from '../components/TaskForm';
import TaskItem from '../components/TaskItem';

export default function TaskDashboard() {
  const [tasks, setTasks] = useState([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [statusFilter, setStatusFilter] = useState('');
  const [showForm, setShowForm] = useState(false);
  const [editingTask, setEditingTask] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const loadTasks = useCallback(async () => {
    setLoading(true);
    setError('');
    try {
      const params = { page, size: 5 };
      if (statusFilter) params.status = statusFilter;
      const { data } = await taskApi.getAll(params);
      setTasks(data.content);
      setTotalPages(data.totalPages);
    } catch (err) {
      setError('Failed to load tasks.');
    } finally {
      setLoading(false);
    }
  }, [page, statusFilter]);

  useEffect(() => {
    loadTasks();
  }, [loadTasks]);

  const handleSave = async (form) => {
    if (editingTask) {
      await taskApi.update(editingTask.id, form);
    } else {
      await taskApi.create(form);
    }
    setShowForm(false);
    setEditingTask(null);
    loadTasks();
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Delete this task?')) return;
    await taskApi.remove(id);
    loadTasks();
  };

  const handleEdit = (task) => {
    setEditingTask(task);
    setShowForm(true);
  };

  return (
    <div className="container">
      <div className="task-header">
        <h2>My Tasks</h2>
        <button className="btn" onClick={() => { setEditingTask(null); setShowForm(true); }}>
          + New Task
        </button>
      </div>

      {showForm && (
        <TaskForm
          initial={editingTask}
          onSave={handleSave}
          onCancel={() => { setShowForm(false); setEditingTask(null); }}
        />
      )}

      <div className="filters">
        <select value={statusFilter} onChange={(e) => { setStatusFilter(e.target.value); setPage(0); }}>
          <option value="">All statuses</option>
          <option value="TODO">To Do</option>
          <option value="IN_PROGRESS">In Progress</option>
          <option value="DONE">Done</option>
        </select>
      </div>

      <div className="card">
        {error && <div className="error-msg">{error}</div>}
        {loading ? (
          <p>Loading...</p>
        ) : tasks.length === 0 ? (
          <p>No tasks yet. Create your first one!</p>
        ) : (
          tasks.map((task) => (
            <TaskItem key={task.id} task={task} onEdit={handleEdit} onDelete={handleDelete} />
          ))
        )}
      </div>

      {totalPages > 1 && (
        <div className="pagination">
          <button className="btn btn-secondary" disabled={page === 0} onClick={() => setPage((p) => p - 1)}>
            Previous
          </button>
          <span style={{ alignSelf: 'center' }}>Page {page + 1} of {totalPages}</span>
          <button
            className="btn btn-secondary"
            disabled={page + 1 >= totalPages}
            onClick={() => setPage((p) => p + 1)}
          >
            Next
          </button>
        </div>
      )}
    </div>
  );
}
