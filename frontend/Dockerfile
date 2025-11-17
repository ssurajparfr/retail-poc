# ---- Build Stage ----
FROM node:24 AS builder
WORKDIR /app

# Step 1: Copy package files for dependency caching
COPY package.json package-lock.json ./

# Step 2: Install dependencies (tolerant to lockfile mismatch)
RUN npm install

# Step 3: Copy rest of the source code
COPY . .

# Step 4: Set backend API URL
ARG REACT_APP_API_BASE_URL
ENV REACT_APP_API_BASE_URL=$REACT_APP_API_BASE_URL

# Step 5: Build production-ready React app
RUN npm run build

# ---- Production Stage ----
FROM nginx:alpine

# Step 6: Copy built app from builder stage
COPY --from=builder /app/build /usr/share/nginx/html

# Step 7: Expose Nginx port
EXPOSE 80

# Step 8: Start Nginx
CMD ["nginx", "-g", "daemon off;"]
