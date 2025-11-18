# Deployment Guide

This guide provides detailed instructions for deploying the User Management Service to various platforms.

## Table of Contents
- [Render.com](#rendercom)
- [Railway.app](#railwayapp)
- [Fly.io](#flyio)
- [AWS ECS](#aws-ecs)
- [Heroku](#heroku)

## Render.com

### Prerequisites
- GitHub account
- Render.com account

### Steps

1. **Push code to GitHub**
```bash
git init
git add .
git commit -m "Initial commit"
git remote add origin https://github.com/yourusername/UserManagement.git
git push -u origin main
```

2. **Create PostgreSQL Database**
   - Go to Render Dashboard
   - Click "New +" → "PostgreSQL"
   - Name: `user-management-db`
   - Database: `usermanagement`
   - User: `postgres`
   - Region: Oregon (Free)
   - Click "Create Database"

3. **Create Web Service**
   - Click "New +" → "Web Service"
   - Connect your GitHub repository
   - Name: `user-management-service`
   - Environment: `Docker`
   - Region: Oregon
   - Instance Type: Free

4. **Set Environment Variables**
   - Add the following environment variables:
   ```
   DATABASE_URL=<internal-database-url-from-render>
   DATABASE_USERNAME=postgres
   DATABASE_PASSWORD=<database-password>
   KAFKA_BOOTSTRAP_SERVERS=<kafka-url-or-skip-for-now>
   ```

5. **Deploy**
   - Click "Create Web Service"
   - Wait for deployment to complete
   - Access your API at: `https://user-management-service.onrender.com`

### Health Check URL
Set health check path to: `/api/v1/health`

## Railway.app

### Prerequisites
- Railway CLI installed: `npm install -g @railway/cli`
- Railway account

### Steps

1. **Login to Railway**
```bash
railway login
```

2. **Initialize Project**
```bash
railway init
```

3. **Add PostgreSQL**
```bash
railway add postgresql
```

4. **Deploy**
```bash
railway up
```

5. **Set Environment Variables**
```bash
railway variables set KAFKA_BOOTSTRAP_SERVERS=localhost:9092
```

6. **Get Deployment URL**
```bash
railway domain
```

### Configuration
The `railway.json` file is already configured in the project.

## Fly.io

### Prerequisites
- Fly CLI installed: `curl -L https://fly.io/install.sh | sh`
- Fly.io account

### Steps

1. **Login to Fly**
```bash
fly auth login
```

2. **Launch App**
```bash
fly launch --name user-management-service
```

3. **Create PostgreSQL Database**
```bash
fly postgres create --name user-management-db
fly postgres attach user-management-db
```

4. **Set Secrets**
```bash
fly secrets set KAFKA_BOOTSTRAP_SERVERS=your-kafka-url
```

5. **Deploy**
```bash
fly deploy
```

6. **Open Application**
```bash
fly open
```

## AWS ECS (Fargate)

### Prerequisites
- AWS CLI configured
- Docker Hub account or AWS ECR

### Steps

1. **Build and Push Docker Image**
```bash
docker build -t user-management:latest .
docker tag user-management:latest yourusername/user-management:latest
docker push yourusername/user-management:latest
```

2. **Create RDS PostgreSQL Instance**
   - Go to AWS RDS Console
   - Create PostgreSQL 16 instance
   - Note down endpoint and credentials

3. **Create ECS Cluster**
```bash
aws ecs create-cluster --cluster-name user-management-cluster
```

4. **Create Task Definition**
   - Use the provided task definition template
   - Update environment variables
   - Register task definition

5. **Create Service**
```bash
aws ecs create-service \
  --cluster user-management-cluster \
  --service-name user-management-service \
  --task-definition user-management:1 \
  --desired-count 1 \
  --launch-type FARGATE
```

## Heroku

### Prerequisites
- Heroku CLI installed
- Heroku account

### Steps

1. **Login to Heroku**
```bash
heroku login
```

2. **Create Application**
```bash
heroku create user-management-service
```

3. **Add PostgreSQL**
```bash
heroku addons:create heroku-postgresql:mini
```

4. **Add Kafka (Optional)**
```bash
heroku addons:create cloudkarafka:ducky
```

5. **Set Buildpack**
```bash
heroku buildpacks:set heroku/gradle
```

6. **Configure Environment**
```bash
heroku config:set JAVA_OPTS="-Xmx512m"
```

7. **Deploy**
```bash
git push heroku main
```

8. **Scale Dyno**
```bash
heroku ps:scale web=1
```

9. **Open Application**
```bash
heroku open
```

## Docker Hub Deployment

### Build Multi-Platform Image

```bash
docker buildx create --use
docker buildx build --platform linux/amd64,linux/arm64 \
  -t yourusername/user-management:latest \
  --push .
```

## Health Checks

All deployment platforms should configure health checks to:
- Path: `/api/v1/health`
- Expected Status: 200
- Interval: 30 seconds
- Timeout: 5 seconds

## Database Migrations

Flyway migrations run automatically on startup. Ensure:
1. Database is accessible before starting the application
2. Proper credentials are set in environment variables
3. Network connectivity between app and database

## Monitoring

Set up monitoring for:
- Application logs
- Database connections
- Kafka message throughput
- API response times
- Error rates

## Scaling Considerations

### Horizontal Scaling
- The application is stateless and can scale horizontally
- Use load balancer for multiple instances
- Ensure database connection pool is properly configured

### Database Scaling
- Consider read replicas for read-heavy workloads
- Use connection pooling (HikariCP is included)
- Monitor slow queries and add indexes as needed

### Kafka Scaling
- Increase partition count for higher throughput
- Use consumer groups for parallel processing
- Monitor lag and adjust accordingly

## Troubleshooting

### Application Won't Start
1. Check database connectivity
2. Verify environment variables
3. Review application logs
4. Ensure Flyway migrations completed

### Database Connection Issues
1. Verify DATABASE_URL format
2. Check firewall rules
3. Confirm credentials
4. Test connection manually

### Kafka Connection Issues
1. Verify KAFKA_BOOTSTRAP_SERVERS
2. Check network connectivity
3. Review Kafka broker logs
4. Temporarily disable Kafka to isolate issue

## Cost Optimization

### Free Tier Options
- **Render**: Free tier available (sleeps after 15 min)
- **Railway**: $5/month free credit
- **Fly.io**: 3 shared VMs free
- **Heroku**: Eco dynos ($5/month)

### Production Recommendations
- Use managed PostgreSQL services
- Consider managed Kafka (Confluent Cloud, AWS MSK)
- Implement caching (Redis)
- Use CDN for static assets
- Enable database connection pooling

## Security Checklist

- [ ] Use HTTPS only
- [ ] Set secure environment variables
- [ ] Enable database SSL
- [ ] Use VPC/private networking
- [ ] Implement rate limiting
- [ ] Enable CORS properly
- [ ] Use secrets management
- [ ] Regular security updates
- [ ] Enable access logs
- [ ] Configure firewall rules

## Post-Deployment Verification

```bash
# Health check
curl https://your-app.com/api/v1/health

# Create test user
curl -X POST https://your-app.com/api/v1/users \
  -H "Content-Type: application/json" \
  -d '{"firstName":"Test","lastName":"User","email":"test@example.com","role":"USER"}'

# Verify user creation
curl https://your-app.com/api/v1/users/1

# Check statistics
curl https://your-app.com/api/v1/users/stats
```

## Rollback Strategy

### Quick Rollback
```bash
# Render: Use dashboard to rollback
# Railway: railway rollback
# Fly.io: fly releases
# Heroku: heroku rollback
```

### Manual Rollback
1. Tag stable versions in Git
2. Redeploy previous Docker image
3. Revert database migrations if needed
4. Monitor application health

## Support

For deployment issues:
1. Check application logs
2. Review platform-specific documentation
3. Contact platform support
4. Open issue on GitHub repository

---

**Last Updated**: November 2025
