import { useState, useEffect } from 'react';
import type { SampleRequest, SampleResponse, SampleType } from '../types/sample';
import { SampleType as SampleTypeEnum } from '../types/sample';

interface SampleFormProps {
  sample?: SampleResponse | null;
  onSubmit: (data: SampleRequest) => Promise<void>;
  onCancel: () => void;
  isSubmitting?: boolean;
}

const SAMPLE_TYPES: SampleType[] = [
  SampleTypeEnum.ROCK,
  SampleTypeEnum.MINERAL,
  SampleTypeEnum.SOIL,
  SampleTypeEnum.FOSSIL,
  SampleTypeEnum.SEDIMENT,
  SampleTypeEnum.OTHER,
];

export default function SampleForm({ sample, onSubmit, onCancel, isSubmitting = false }: SampleFormProps) {
  const [formData, setFormData] = useState<SampleRequest>({
    sampleIdentifier: '',
    sampleName: '',
    sampleType: SampleTypeEnum.ROCK,
    collectionDate: '',
    latitude: undefined,
    longitude: undefined,
    locationName: '',
    collectorName: '',
    description: '',
    storageLocation: '',
  });

  const [errors, setErrors] = useState<Record<string, string>>({});

  useEffect(() => {
    if (sample) {
      // Format date for input (YYYY-MM-DD)
      const collectionDate = sample.collectionDate.split('T')[0];
      setFormData({
        sampleIdentifier: sample.sampleIdentifier,
        sampleName: sample.sampleName,
        sampleType: sample.sampleType,
        collectionDate,
        latitude: sample.latitude,
        longitude: sample.longitude,
        locationName: sample.locationName || '',
        collectorName: sample.collectorName,
        description: sample.description || '',
        storageLocation: sample.storageLocation || '',
      });
    }
  }, [sample]);

  const validate = (): boolean => {
    const newErrors: Record<string, string> = {};

    if (!formData.sampleIdentifier.trim()) {
      newErrors.sampleIdentifier = 'Sample identifier is required';
    }
    if (!formData.sampleName.trim()) {
      newErrors.sampleName = 'Sample name is required';
    }
    if (!formData.collectionDate) {
      newErrors.collectionDate = 'Collection date is required';
    }
    if (!formData.collectorName.trim()) {
      newErrors.collectorName = 'Collector name is required';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!validate()) {
      return;
    }

    // Clean up undefined values
    const cleanedData: SampleRequest = {
      ...formData,
      latitude: formData.latitude || undefined,
      longitude: formData.longitude || undefined,
      locationName: formData.locationName || undefined,
      description: formData.description || undefined,
      storageLocation: formData.storageLocation || undefined,
    };

    await onSubmit(cleanedData);
  };

  const handleChange = (field: keyof SampleRequest, value: string | number | SampleType | undefined) => {
    setFormData((prev) => ({ ...prev, [field]: value }));
    // Clear error when user starts typing
    if (errors[field]) {
      setErrors((prev) => ({ ...prev, [field]: '' }));
    }
  };

  return (
    <div className="max-w-2xl mx-auto bg-white rounded-lg shadow-md p-6">
      <h2 className="text-2xl font-bold text-gray-900 mb-6">
        {sample ? 'Edit Sample' : 'Create New Sample'}
      </h2>

      <form onSubmit={handleSubmit} className="space-y-6">
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          <div>
            <label htmlFor="sampleIdentifier" className="block text-sm font-medium text-gray-700 mb-1">
              Sample Identifier <span className="text-red-500">*</span>
            </label>
            <input
              type="text"
              id="sampleIdentifier"
              value={formData.sampleIdentifier}
              onChange={(e) => handleChange('sampleIdentifier', e.target.value)}
              className={`w-full px-3 py-2 bg-white text-gray-900 border rounded-md focus:outline-none focus:ring-2 focus:ring-primary-500 ${
                errors.sampleIdentifier ? 'border-red-500' : 'border-gray-300'
              }`}
              disabled={isSubmitting}
            />
            {errors.sampleIdentifier && (
              <p className="mt-1 text-sm text-red-600">{errors.sampleIdentifier}</p>
            )}
          </div>

          <div>
            <label htmlFor="sampleName" className="block text-sm font-medium text-gray-700 mb-1">
              Sample Name <span className="text-red-500">*</span>
            </label>
            <input
              type="text"
              id="sampleName"
              value={formData.sampleName}
              onChange={(e) => handleChange('sampleName', e.target.value)}
              className={`w-full px-3 py-2 bg-white text-gray-900 border rounded-md focus:outline-none focus:ring-2 focus:ring-primary-500 ${
                errors.sampleName ? 'border-red-500' : 'border-gray-300'
              }`}
              disabled={isSubmitting}
            />
            {errors.sampleName && (
              <p className="mt-1 text-sm text-red-600">{errors.sampleName}</p>
            )}
          </div>

          <div>
            <label htmlFor="sampleType" className="block text-sm font-medium text-gray-700 mb-1">
              Sample Type <span className="text-red-500">*</span>
            </label>
            <select
              id="sampleType"
              value={formData.sampleType}
              onChange={(e) => handleChange('sampleType', e.target.value as SampleType)}
              className="w-full px-3 py-2 bg-white text-gray-900 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-primary-500"
              disabled={isSubmitting}
            >
              {SAMPLE_TYPES.map((type) => (
                <option key={type} value={type}>
                  {type}
                </option>
              ))}
            </select>
          </div>

          <div>
            <label htmlFor="collectionDate" className="block text-sm font-medium text-gray-700 mb-1">
              Collection Date <span className="text-red-500">*</span>
            </label>
            <input
              type="date"
              id="collectionDate"
              value={formData.collectionDate}
              onChange={(e) => handleChange('collectionDate', e.target.value)}
              className={`w-full px-3 py-2 bg-white text-gray-900 border rounded-md focus:outline-none focus:ring-2 focus:ring-primary-500 ${
                errors.collectionDate ? 'border-red-500' : 'border-gray-300'
              }`}
              disabled={isSubmitting}
            />
            {errors.collectionDate && (
              <p className="mt-1 text-sm text-red-600">{errors.collectionDate}</p>
            )}
          </div>

          <div>
            <label htmlFor="latitude" className="block text-sm font-medium text-gray-700 mb-1">
              Latitude
            </label>
            <input
              type="number"
              id="latitude"
              step="any"
              value={formData.latitude || ''}
              onChange={(e) => handleChange('latitude', e.target.value ? parseFloat(e.target.value) : undefined)}
              className="w-full px-3 py-2 bg-white text-gray-900 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-primary-500"
              disabled={isSubmitting}
            />
          </div>

          <div>
            <label htmlFor="longitude" className="block text-sm font-medium text-gray-700 mb-1">
              Longitude
            </label>
            <input
              type="number"
              id="longitude"
              step="any"
              value={formData.longitude || ''}
              onChange={(e) => handleChange('longitude', e.target.value ? parseFloat(e.target.value) : undefined)}
              className="w-full px-3 py-2 bg-white text-gray-900 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-primary-500"
              disabled={isSubmitting}
            />
          </div>

          <div>
            <label htmlFor="locationName" className="block text-sm font-medium text-gray-700 mb-1">
              Location Name
            </label>
            <input
              type="text"
              id="locationName"
              value={formData.locationName}
              onChange={(e) => handleChange('locationName', e.target.value)}
              className="w-full px-3 py-2 bg-white text-gray-900 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-primary-500"
              disabled={isSubmitting}
            />
          </div>

          <div>
            <label htmlFor="collectorName" className="block text-sm font-medium text-gray-700 mb-1">
              Collector Name <span className="text-red-500">*</span>
            </label>
            <input
              type="text"
              id="collectorName"
              value={formData.collectorName}
              onChange={(e) => handleChange('collectorName', e.target.value)}
              className={`w-full px-3 py-2 bg-white text-gray-900 border rounded-md focus:outline-none focus:ring-2 focus:ring-primary-500 ${
                errors.collectorName ? 'border-red-500' : 'border-gray-300'
              }`}
              disabled={isSubmitting}
            />
            {errors.collectorName && (
              <p className="mt-1 text-sm text-red-600">{errors.collectorName}</p>
            )}
          </div>

          <div className="md:col-span-2">
            <label htmlFor="description" className="block text-sm font-medium text-gray-700 mb-1">
              Description
            </label>
            <textarea
              id="description"
              rows={3}
              value={formData.description}
              onChange={(e) => handleChange('description', e.target.value)}
              className="w-full px-3 py-2 bg-white text-gray-900 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-primary-500"
              disabled={isSubmitting}
            />
          </div>

          <div className="md:col-span-2">
            <label htmlFor="storageLocation" className="block text-sm font-medium text-gray-700 mb-1">
              Storage Location
            </label>
            <input
              type="text"
              id="storageLocation"
              value={formData.storageLocation}
              onChange={(e) => handleChange('storageLocation', e.target.value)}
              className="w-full px-3 py-2 bg-white text-gray-900 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-primary-500"
              disabled={isSubmitting}
            />
          </div>
        </div>

        <div className="flex gap-3 justify-end pt-4 border-t border-gray-200">
          <button
            type="button"
            onClick={onCancel}
            className="px-6 py-2 border border-gray-300 rounded-md text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-gray-500 disabled:opacity-50"
            disabled={isSubmitting}
          >
            Cancel
          </button>
          <button
            type="submit"
            className="px-6 py-2 bg-primary-600 text-white rounded-md hover:bg-primary-700 focus:outline-none focus:ring-2 focus:ring-primary-500 disabled:opacity-50"
            disabled={isSubmitting}
          >
            {isSubmitting ? 'Saving...' : sample ? 'Update Sample' : 'Create Sample'}
          </button>
        </div>
      </form>
    </div>
  );
}
