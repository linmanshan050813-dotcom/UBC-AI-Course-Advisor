# Frontend-Backend Connection Setup

## Phase 0 Configuration

### Development Mode (Current)

In development, the frontend uses **Vite proxy** to automatically route API requests to the backend.

**How it works:**
1. Frontend runs on `http://localhost:3000`
2. Backend runs on `http://localhost:8080`
3. Vite proxy intercepts requests to `/api/*` and `/health` and forwards them to `http://localhost:8080`

**No environment variable needed in development!**

The `apiClient.ts` automatically uses relative paths in development mode, which are handled by the Vite proxy.

### Production Mode

In production builds, the frontend will use the `VITE_API_BASE_URL` environment variable to determine the backend URL.

### Testing the Connection

1. **Start Backend:**
   ```bash
   cd backend
   mvn spring-boot:run
   ```

2. **Start Frontend:**
   ```bash
   cd frontend
   npm install
   npm run dev
   ```

3. **Test Endpoints:**
   - Visit `http://localhost:3000` - Should show health check
   - Visit `http://localhost:3000/test` - Should allow testing all mock endpoints
   - All requests should succeed without CORS errors

### Troubleshooting

**If you see CORS errors:**
- Ensure backend is running on port 8080
- Check that `CorsConfig.java` is properly configured
- Verify Vite proxy is working (check browser Network tab)

**If requests fail:**
- Check browser console for errors
- Verify backend is running: `curl http://localhost:8080/health`
- Check Vite dev server console for proxy errors

