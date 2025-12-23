import { useState } from 'react';
import { sampleApi } from './services/api';
import type { SampleRequest, SampleResponse } from './types/sample';
import SampleList from './components/SampleList';
import SampleForm from './components/SampleForm';
import DeleteConfirm from './components/DeleteConfirm';

type View = 'list' | 'form';

function App() {
  const [currentView, setCurrentView] = useState<View>('list');
  const [editingSample, setEditingSample] = useState<SampleResponse | null>(null);
  const [deleteSample, setDeleteSample] = useState<SampleResponse | null>(null);
  const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);
  const [isDeleting, setIsDeleting] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [notification, setNotification] = useState<{ type: 'success' | 'error'; message: string } | null>(null);

  const showNotification = (type: 'success' | 'error', message: string) => {
    setNotification({ type, message });
    setTimeout(() => setNotification(null), 5000);
  };

  const handleCreateNew = () => {
    setEditingSample(null);
    setCurrentView('form');
  };

  const handleEdit = (sample: SampleResponse) => {
    setEditingSample(sample);
    setCurrentView('form');
  };

  const handleDeleteClick = (sample: SampleResponse) => {
    setDeleteSample(sample);
    setIsDeleteModalOpen(true);
  };

  const handleDeleteConfirm = async () => {
    if (!deleteSample) return;

    try {
      setIsDeleting(true);
      await sampleApi.deleteSample(deleteSample.id);
      showNotification('success', 'Sample deleted successfully');
      setIsDeleteModalOpen(false);
      setDeleteSample(null);
      // Refresh the list by going back to list view
      setCurrentView('list');
      // Force a page reload to refresh the list
      window.location.reload();
    } catch (error) {
      showNotification('error', error instanceof Error ? error.message : 'Failed to delete sample');
    } finally {
      setIsDeleting(false);
    }
  };

  const handleFormSubmit = async (data: SampleRequest) => {
    try {
      setIsSubmitting(true);
      if (editingSample) {
        await sampleApi.updateSample(editingSample.id, data);
        showNotification('success', 'Sample updated successfully');
      } else {
        await sampleApi.createSample(data);
        showNotification('success', 'Sample created successfully');
      }
      setCurrentView('list');
      setEditingSample(null);
      // Force a page reload to refresh the list
      window.location.reload();
    } catch (error) {
      showNotification('error', error instanceof Error ? error.message : 'Failed to save sample');
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleFormCancel = () => {
    setCurrentView('list');
    setEditingSample(null);
  };

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Notification */}
      {notification && (
        <div className="fixed top-4 right-4 z-50 animate-in slide-in-from-top-5">
          <div
            className={`rounded-md p-4 shadow-lg ${
              notification.type === 'success'
                ? 'bg-green-50 text-green-800 border border-green-200'
                : 'bg-red-50 text-red-800 border border-red-200'
            }`}
          >
            <div className="flex">
              <div className="flex-shrink-0">
                {notification.type === 'success' ? (
                  <svg className="h-5 w-5 text-green-400" viewBox="0 0 20 20" fill="currentColor">
                    <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clipRule="evenodd" />
                  </svg>
                ) : (
                  <svg className="h-5 w-5 text-red-400" viewBox="0 0 20 20" fill="currentColor">
                    <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clipRule="evenodd" />
                  </svg>
                )}
              </div>
              <div className="ml-3">
                <p className="text-sm font-medium">{notification.message}</p>
              </div>
              <div className="ml-auto pl-3">
                <button
                  onClick={() => setNotification(null)}
                  className="inline-flex rounded-md p-1.5 hover:bg-opacity-20 focus:outline-none"
                >
                  <span className="sr-only">Dismiss</span>
                  <svg className="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
                    <path fillRule="evenodd" d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z" clipRule="evenodd" />
                  </svg>
                </button>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* Header */}
      <header className="bg-white shadow-sm border-b border-gray-200">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-4">
          <div className="flex justify-between items-center">
            <h1 className="text-2xl font-bold text-gray-900">Geological Sample Management</h1>
            {currentView === 'list' && (
              <button
                onClick={handleCreateNew}
                className="px-4 py-2 bg-primary-600 text-white rounded-md hover:bg-primary-700 transition-colors font-medium"
              >
                + Add New Sample
              </button>
            )}
            {currentView === 'form' && (
              <button
                onClick={handleFormCancel}
                className="px-4 py-2 border border-gray-300 text-gray-700 rounded-md hover:bg-gray-50 transition-colors font-medium"
              >
                ← Back to List
              </button>
            )}
          </div>
        </div>
      </header>

      {/* Main Content */}
      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {currentView === 'list' ? (
          <SampleList onEdit={handleEdit} onDelete={handleDeleteClick} />
        ) : (
          <SampleForm
            sample={editingSample}
            onSubmit={handleFormSubmit}
            onCancel={handleFormCancel}
            isSubmitting={isSubmitting}
          />
        )}
      </main>

      {/* Delete Confirmation Modal */}
      <DeleteConfirm
        isOpen={isDeleteModalOpen}
        sample={deleteSample}
        onClose={() => {
          setIsDeleteModalOpen(false);
          setDeleteSample(null);
        }}
        onConfirm={handleDeleteConfirm}
        isDeleting={isDeleting}
      />
    </div>
  );
}

export default App;
