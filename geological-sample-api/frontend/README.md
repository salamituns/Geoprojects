# Geological Sample Management - Frontend

A modern React + TypeScript frontend application for managing geological samples. Built with Vite, Tailwind CSS, and Axios.

## Features

- **CRUD Operations**: Create, read, update, and delete geological samples
- **Responsive Design**: Works seamlessly on desktop, tablet, and mobile devices
- **Pagination**: Efficiently browse through large collections of samples
- **Form Validation**: Client-side validation with helpful error messages
- **Error Handling**: User-friendly error messages and loading states
- **Modern UI**: Clean, accessible interface built with Tailwind CSS

## Prerequisites

- **Node.js 18+** - Required for running the frontend
- **npm** or **yarn** - Package manager
- **Backend API** - The Spring Boot API should be running on `http://localhost:8080`

## Setup

### 1. Install Dependencies

```bash
cd frontend
npm install
```

### 2. Configure API URL (Optional)

The frontend is configured to connect to `http://localhost:8080` by default. If your backend is running on a different URL, create a `.env` file:

```bash
VITE_API_BASE_URL=http://localhost:8080
```

### 3. Start Development Server

```bash
npm run dev
```

The frontend will be available at `http://localhost:5173`

## Available Scripts

- `npm run dev` - Start development server with hot module replacement
- `npm run build` - Build the application for production
- `npm run preview` - Preview the production build locally

## Project Structure

```
frontend/
├── src/
│   ├── components/          # React components
│   │   ├── SampleList.tsx   # Main list view with pagination
│   │   ├── SampleCard.tsx   # Individual sample card
│   │   ├── SampleForm.tsx   # Create/Edit form
│   │   └── DeleteConfirm.tsx # Delete confirmation modal
│   ├── services/
│   │   └── api.ts           # API client with axios
│   ├── types/
│   │   └── sample.ts        # TypeScript type definitions
│   ├── App.tsx              # Main application component
│   ├── index.tsx            # Application entry point
│   └── index.css            # Tailwind CSS imports
├── public/                  # Static assets
├── package.json
├── vite.config.ts          # Vite configuration
├── tailwind.config.js      # Tailwind CSS configuration
└── tsconfig.json           # TypeScript configuration
```

## Technology Stack

- **React 18** - UI library
- **TypeScript** - Type safety
- **Vite** - Build tool and dev server
- **Tailwind CSS** - Utility-first CSS framework
- **Axios** - HTTP client
- **Headless UI** - Accessible UI components

## Development Workflow

1. **Start the Backend**: In the project root, run `make run` to start the Spring Boot API
2. **Start the Frontend**: In the `frontend/` directory, run `npm run dev`
3. **Access the Application**: Open `http://localhost:5173` in your browser

## Building for Production

```bash
npm run build
```

The production build will be in the `dist/` directory. You can preview it with:

```bash
npm run preview
```

## API Integration

The frontend communicates with the backend API through the service layer in `src/services/api.ts`. All API calls are typed and include error handling.

### Endpoints Used

- `GET /api/v1/samples` - Get paginated list of samples
- `GET /api/v1/samples/{id}` - Get a single sample
- `POST /api/v1/samples` - Create a new sample
- `PUT /api/v1/samples/{id}` - Update an existing sample
- `DELETE /api/v1/samples/{id}` - Delete a sample

## CORS Configuration

The backend has been configured to allow requests from the frontend. The CORS configuration allows:
- Origins: `http://localhost:5173` (Vite default) and `http://localhost:3000`
- Methods: GET, POST, PUT, DELETE, OPTIONS
- Headers: All headers
- Credentials: Enabled

## Troubleshooting

### CORS Errors

If you encounter CORS errors, ensure:
1. The backend CORS configuration is correct (see `CorsConfig.java`)
2. The backend is running on `http://localhost:8080`
3. The frontend is running on `http://localhost:5173`

### API Connection Issues

- Verify the backend is running: `curl http://localhost:8080/healthcheck`
- Check the browser console for detailed error messages
- Ensure the API base URL in `src/services/api.ts` matches your backend URL

### Build Errors

- Clear node_modules and reinstall: `rm -rf node_modules && npm install`
- Clear Vite cache: `rm -rf node_modules/.vite`

## Future Enhancements

- Authentication and authorization
- Advanced filtering and search
- Bulk operations
- Export functionality
- Real-time updates
- Image upload for samples
