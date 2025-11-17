# ---- Build Stage ----
FROM node:24 AS builder
WORKDIR /app

# Copy package files first for caching
COPY package*.json ./

# Install dependencies using npm ci (faster & deterministic)
RUN npm ci

# Copy the rest of the source code
COPY . .

# Build the production-ready React app
ARG REACT_APP_API_BASE_URL
ENV REACT_APP_API_BASE_URL=$REACT_APP_API_BASE_URL
RUN npm run build

# ---- Production Stage ----
FROM nginx:alpine

# Copy built files from builder stage
COPY --from=builder /app/build /usr/share/nginx/html

# Expose Nginx port
EXPOSE 80

# Start Nginx
CMD ["nginx", "-g", "daemon off;"]
