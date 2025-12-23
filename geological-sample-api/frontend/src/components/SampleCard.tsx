import type { SampleResponse } from '../types/sample';
import { SampleType } from '../types/sample';

interface SampleCardProps {
  sample: SampleResponse;
  onEdit: (sample: SampleResponse) => void;
  onDelete: (sample: SampleResponse) => void;
}

const getSampleTypeColor = (type: SampleType): string => {
  const colors: Record<SampleType, string> = {
    [SampleType.ROCK]: 'bg-gray-100 text-gray-800',
    [SampleType.MINERAL]: 'bg-yellow-100 text-yellow-800',
    [SampleType.SOIL]: 'bg-amber-100 text-amber-800',
    [SampleType.FOSSIL]: 'bg-green-100 text-green-800',
    [SampleType.SEDIMENT]: 'bg-blue-100 text-blue-800',
    [SampleType.OTHER]: 'bg-purple-100 text-purple-800',
  };
  return colors[type] || colors[SampleType.OTHER];
};

export default function SampleCard({ sample, onEdit, onDelete }: SampleCardProps) {
  const formatDate = (dateString: string): string => {
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
    });
  };

  return (
    <div className="bg-white rounded-lg shadow-md hover:shadow-lg transition-shadow p-6 border border-gray-200">
      <div className="flex justify-between items-start mb-4">
        <div>
          <h3 className="text-lg font-semibold text-gray-900">{sample.sampleIdentifier}</h3>
          <p className="text-gray-700 mt-1">{sample.sampleName}</p>
        </div>
        <span className={`px-3 py-1 rounded-full text-xs font-medium ${getSampleTypeColor(sample.sampleType)}`}>
          {sample.sampleType}
        </span>
      </div>

      <div className="space-y-2 text-sm text-gray-600 mb-4">
        {sample.collectionDate && (
          <div className="flex items-center">
            <span className="font-medium mr-2">Collection Date:</span>
            <span>{formatDate(sample.collectionDate)}</span>
          </div>
        )}
        {sample.locationName && (
          <div className="flex items-center">
            <span className="font-medium mr-2">Location:</span>
            <span>{sample.locationName}</span>
          </div>
        )}
        {sample.latitude && sample.longitude && (
          <div className="flex items-center">
            <span className="font-medium mr-2">Coordinates:</span>
            <span>{sample.latitude.toFixed(4)}, {sample.longitude.toFixed(4)}</span>
          </div>
        )}
        <div className="flex items-center">
          <span className="font-medium mr-2">Collector:</span>
          <span>{sample.collectorName}</span>
        </div>
        {sample.storageLocation && (
          <div className="flex items-center">
            <span className="font-medium mr-2">Storage:</span>
            <span>{sample.storageLocation}</span>
          </div>
        )}
      </div>

      {sample.description && (
        <p className="text-sm text-gray-500 mb-4 line-clamp-2">{sample.description}</p>
      )}

      <div className="flex gap-2 pt-4 border-t border-gray-200">
        <button
          onClick={() => onEdit(sample)}
          className="flex-1 px-4 py-2 bg-primary-600 text-white rounded-md hover:bg-primary-700 transition-colors text-sm font-medium"
        >
          Edit
        </button>
        <button
          onClick={() => onDelete(sample)}
          className="flex-1 px-4 py-2 bg-red-600 text-white rounded-md hover:bg-red-700 transition-colors text-sm font-medium"
        >
          Delete
        </button>
      </div>
    </div>
  );
}
