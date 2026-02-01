# 构建阶段：使用 Node.js 镜像编译前端
FROM node:18-alpine AS builder

WORKDIR /app

# 定义构建参数：后端API URL（用于生产构建）
# 使用空字符串以启用相对路径（Nginx 会代理到后端）
ARG VITE_API_BASE_URL=
ENV VITE_API_BASE_URL=$VITE_API_BASE_URL

# Supabase Configuration
ARG VITE_SUPABASE_URL
ENV VITE_SUPABASE_URL=$VITE_SUPABASE_URL
ARG VITE_SUPABASE_ANON_KEY
ENV VITE_SUPABASE_ANON_KEY=$VITE_SUPABASE_ANON_KEY

# 复制 package 文件
COPY package*.json ./

# 安装依赖
RUN npm ci

# 复制源代码
COPY . .

# 构建生产版本（VITE_API_BASE_URL将在构建时嵌入到代码中）
RUN npm run build

# 运行阶段：使用 Nginx 提供静态文件服务
FROM nginx:alpine

# 复制构建产物到 Nginx 目录
COPY --from=builder /app/dist /usr/share/nginx/html

# 复制 Nginx 配置文件（用于 API 代理）
COPY nginx.conf /etc/nginx/conf.d/default.conf

# 暴露端口
EXPOSE 80

# Nginx 会自动启动，无需额外命令
