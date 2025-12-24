#!/bin/bash

################################################################################
# AuraSkills SkillCoins Deployment Script
# Builds and deploys AuraSkills with SkillCoins to Pterodactyl server
################################################################################

# Configuration
PROJECT_DIR="/root/WDP-Rework/SkillCoins/AuraSkills-Coins"
CONTAINER_NAME="b8f24891-b5be-4847-a96e-c705c500aece"  # Use container name
SERVER_DIR="/var/lib/pterodactyl/volumes/b8f24891-b5be-4847-a96e-c705c500aece"
PLUGINS_DIR="${SERVER_DIR}/plugins"
JAR_NAME="AuraSkills-2.3.8.jar"

# Colors
readonly RED='\033[0;31m'
readonly GREEN='\033[0;32m'
readonly YELLOW='\033[1;33m'
readonly BLUE='\033[0;34m'
readonly CYAN='\033[0;36m'
readonly BOLD='\033[1m'
readonly NC='\033[0m'

# Logging functions
log_step() {
    echo -e "${BLUE}${BOLD}[STEP]${NC} $1"
}

log_success() {
    echo -e "${GREEN}${BOLD}[✓]${NC} $1"
}

log_error() {
    echo -e "${RED}${BOLD}[✗]${NC} $1"
}

log_warning() {
    echo -e "${YELLOW}${BOLD}[!]${NC} $1"
}

log_info() {
    echo -e "${CYAN}[INFO]${NC} $1"
}

# Check root
if [[ $EUID -ne 0 ]]; then 
    log_error "This script must be run as root"
    exit 1
fi

echo ""
echo -e "${CYAN}╔══════════════════════════════════════════════════════════╗${NC}"
echo -e "${CYAN}║         AuraSkills SkillCoins Deployment Script          ║${NC}"
echo -e "${CYAN}╚══════════════════════════════════════════════════════════╝${NC}"
echo ""

################################################################################
# STEP 1: Build Project
################################################################################
log_step "Building AuraSkills project..."
cd "$PROJECT_DIR" || exit 1

BUILD_LOG="/tmp/auraskills_build_$(date +%s).log"
./gradlew clean build -x test > "$BUILD_LOG" 2>&1
BUILD_EXIT=$?

if [[ $BUILD_EXIT -eq 0 ]] && grep -q "BUILD SUCCESSFUL" "$BUILD_LOG"; then
    log_success "Build completed successfully"
elif [[ $BUILD_EXIT -eq 0 ]]; then
    log_success "Build completed (with warnings)"
else
    log_error "Build failed! Check log: $BUILD_LOG"
    tail -n 50 "$BUILD_LOG" | grep -A 10 -B 3 -i "error\|failed"
    exit 1
fi

# Verify JAR exists
BUILD_JAR="${PROJECT_DIR}/bukkit/build/libs/${JAR_NAME}"
if [[ ! -f "$BUILD_JAR" ]]; then
    log_error "Built JAR not found: $BUILD_JAR"
    log_info "Available JARs in build directory:"
    ls -la "${PROJECT_DIR}/bukkit/build/libs/" | grep -E "\.jar$" || echo "  No JAR files found"
    exit 1
fi

JAR_SIZE=$(du -h "$BUILD_JAR" | cut -f1)
log_success "JAR built: ${JAR_NAME} (${JAR_SIZE})"

################################################################################
# STEP 2: Stop Container
################################################################################
log_step "Stopping Minecraft server container..."

# Check if container exists
if ! docker ps -a --format '{{.Names}}' | grep -q "^${CONTAINER_NAME}$"; then
    log_error "Container not found: ${CONTAINER_NAME}"
    log_info "Available containers:"
    docker ps -a --format "  {{.Names}}" | head -10
    exit 1
fi

# Get the actual container ID
CONTAINER_ID=$(docker ps -a --format '{{.Names}} {{.ID}}' | grep "^${CONTAINER_NAME}" | awk '{print $2}')
if [[ -z "$CONTAINER_ID" ]]; then
    log_error "Could not extract container ID"
    exit 1
fi
log_info "Found container: ${CONTAINER_NAME} (${CONTAINER_ID})"

# Function to check if container is running using docker inspect
is_running() {
    local status=$(docker inspect -f '{{.State.Running}}' "$CONTAINER_ID" 2>/dev/null)
    [[ "$status" == "true" ]]
}

# Function to get container status
get_container_status() {
    docker inspect -f '{{.State.Status}}' "$CONTAINER_ID" 2>/dev/null || echo "unknown"
}

# Check if container is running
if is_running; then
    log_info "Container is running, initiating stop..."
    
    # Send stop signal
    docker stop "$CONTAINER_NAME" >/dev/null 2>&1
    
    # Wait for container to stop with timeout
    TIMEOUT=60
    ELAPSED=0
    
    while [[ $ELAPSED -lt $TIMEOUT ]]; do
        # Check if container stopped
        if ! is_running; then
            echo ""  # New line after progress
            log_success "Container stopped gracefully (took ${ELAPSED}s)"
            break
        fi
        
        # Show progress every 3 seconds
        if (( ELAPSED % 3 == 0 )); then
            echo -ne "  ${CYAN}└─${NC} Waiting for graceful shutdown... ${ELAPSED}s / ${TIMEOUT}s\r"
        fi
        
        sleep 1
        ((ELAPSED++))
    done
    
    # Force kill if still running after timeout
    if is_running; then
        echo ""  # New line after progress
        log_warning "Graceful shutdown timeout (${TIMEOUT}s), forcing stop..."
        docker kill "$CONTAINER_NAME" >/dev/null 2>&1
        sleep 3
        
        # Final check
        if ! is_running; then
            log_success "Container force stopped"
        else
            log_error "Failed to stop container!"
            exit 1
        fi
    fi
else
    CONTAINER_STATUS=$(get_container_status)
    log_info "Container already stopped (status: ${CONTAINER_STATUS})"
fi

# Wait for filesystem to be ready
log_info "Waiting for filesystem to sync..."
sleep 3

# Verify server directory exists
if [[ ! -d "$SERVER_DIR" ]]; then
    log_error "Server directory not found: $SERVER_DIR"
    log_info "Available directories:"
    ls -la /var/lib/pterodactyl/volumes/ 2>/dev/null | head -10 || echo "  No volumes directory found"
    exit 1
fi

# Verify plugins directory exists
if [[ ! -d "$PLUGINS_DIR" ]]; then
    log_error "Plugins directory not found: $PLUGINS_DIR"
    log_info "Creating plugins directory..."
    mkdir -p "$PLUGINS_DIR"
    if [[ ! -d "$PLUGINS_DIR" ]]; then
        log_error "Failed to create plugins directory"
        exit 1
    fi
    log_success "Plugins directory created"
fi

################################################################################
# STEP 3: Backup Current Plugin
################################################################################
if [[ -f "${PLUGINS_DIR}/${JAR_NAME}" ]]; then
    log_step "Backing up current plugin..."
    BACKUP_DIR="${SERVER_DIR}/backups/auraskills"
    mkdir -p "$BACKUP_DIR"
    TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
    cp "${PLUGINS_DIR}/${JAR_NAME}" "${BACKUP_DIR}/${JAR_NAME}.${TIMESTAMP}.bak"
    log_success "Backup created: ${JAR_NAME}.${TIMESTAMP}.bak"
fi

################################################################################
# STEP 4: Clean Old Installation
################################################################################
log_step "Removing old plugin files..."

# Remove old AuraSkills folder
if [[ -d "${PLUGINS_DIR}/AuraSkills" ]]; then
    log_info "Deleting: ${PLUGINS_DIR}/AuraSkills"
    rm -rf "${PLUGINS_DIR}/AuraSkills"
    
    if [[ -d "${PLUGINS_DIR}/AuraSkills" ]]; then
        log_error "Failed to remove AuraSkills directory"
        exit 1
    fi
    log_success "AuraSkills data folder removed"
fi

# Remove old JAR
if [[ -f "${PLUGINS_DIR}/${JAR_NAME}" ]]; then
    rm -f "${PLUGINS_DIR}/${JAR_NAME}"
    log_success "Old JAR removed"
fi

################################################################################
# STEP 5: Deploy New Plugin
################################################################################
log_step "Deploying new plugin..."

# Copy JAR
log_info "Copying JAR to plugins directory..."
if ! cp "$BUILD_JAR" "${PLUGINS_DIR}/"; then
    log_error "Failed to copy JAR to plugins directory"
    exit 1
fi

# Verify copy
if [[ ! -f "${PLUGINS_DIR}/${JAR_NAME}" ]]; then
    log_error "Failed to copy JAR to plugins directory"
    exit 1
fi

# Set ownership
if ! chown pterodactyl:pterodactyl "${PLUGINS_DIR}/${JAR_NAME}"; then
    log_warning "Failed to set ownership (may not be critical)"
else
    log_success "Plugin JAR deployed and ownership set"
fi

################################################################################
# STEP 6: Start Container
################################################################################
log_step "Starting Minecraft server container..."

# Start container
log_info "Starting container..."
if ! docker start "$CONTAINER_NAME" >/dev/null 2>&1; then
    log_error "Failed to send start command to container"
    exit 1
fi

# Verify container starts using docker inspect
TIMEOUT=60
ELAPSED=0

while [[ $ELAPSED -lt $TIMEOUT ]]; do
    # Use the is_running function from earlier
    if is_running; then
        echo ""  # New line after progress
        log_success "Container started successfully (took ${ELAPSED}s)"
        break
    fi
    
    if (( ELAPSED % 5 == 0 )) && (( ELAPSED > 0 )); then
        echo -ne "  ${CYAN}└─${NC} Waiting for container to start... ${ELAPSED}s / ${TIMEOUT}s\r"
    fi
    
    sleep 1
    ((ELAPSED++))
done

if ! is_running; then
    echo ""  # New line after progress
    log_error "Container failed to start within ${TIMEOUT}s"
    log_error "Check logs: docker logs ${CONTAINER_NAME}"
    exit 1
fi

################################################################################
# STEP 7: Monitor Server Startup
################################################################################
log_step "Monitoring server startup..."

LOG_FILE="${SERVER_DIR}/logs/latest.log"
START_TIMEOUT=120
START_ELAPSED=0

# Wait for log file
while [[ ! -f "$LOG_FILE" ]] && [[ $START_ELAPSED -lt 20 ]]; do
    sleep 1
    ((START_ELAPSED++))
done

if [[ -f "$LOG_FILE" ]]; then
    log_info "Server log found, monitoring startup..."
    
    # Monitor for "Done" message
    while [[ $START_ELAPSED -lt $START_TIMEOUT ]]; do
        if grep -q "Done (" "$LOG_FILE" 2>/dev/null; then
            SERVER_TIME=$(grep "Done (" "$LOG_FILE" | tail -1 | grep -oP '\(\K[^)]+' || echo "unknown")
            log_success "Server started successfully! (${SERVER_TIME})"
            break
        fi
        
        # Check for errors
        if grep -qi "error\|exception\|failed" "$LOG_FILE" 2>/dev/null | tail -5 | grep -qi "auraskills"; then
            log_warning "Potential errors detected in log, check console"
        fi
        
        if (( START_ELAPSED % 10 == 0 )) && (( START_ELAPSED > 0 )); then
            echo -ne "  ${CYAN}└─${NC} Waiting for server startup... ${START_ELAPSED}s\r"
        fi
        
        sleep 1
        ((START_ELAPSED++))
    done
    echo ""
    
    if [[ $START_ELAPSED -ge $START_TIMEOUT ]]; then
        log_warning "Server startup monitoring timeout (${START_TIMEOUT}s)"
        log_info "Server may still be starting, check Pterodactyl console"
    fi
else
    log_warning "Server log not found, unable to monitor startup"
    log_info "Check Pterodactyl console for status"
fi

################################################################################
# Deployment Complete
################################################################################
echo ""
echo -e "${GREEN}╔══════════════════════════════════════════════════════════╗${NC}"
echo -e "${GREEN}║              DEPLOYMENT COMPLETED SUCCESSFULLY           ║${NC}"
echo -e "${GREEN}╚══════════════════════════════════════════════════════════╝${NC}"
echo ""

log_info "Deployment Summary:"
echo "  ✓ Plugin: ${PLUGINS_DIR}/${JAR_NAME} (${JAR_SIZE})"
echo "  ✓ Container: Running"
echo "  ✓ Config: Will be auto-generated on first load"
echo ""

log_info "Next Steps:"
echo "  1. ${YELLOW}Monitor server console${NC} in Pterodactyl"
echo "  2. ${YELLOW}Test shop:${NC} /shop"
echo "  3. ${YELLOW}Check balance:${NC} /skillcoins balance"
echo "  4. ${YELLOW}Give coins:${NC} /skillcoins give <player> coins 1000"
echo ""

log_info "Logs:"
echo "  • Build: ${BUILD_LOG}"
echo "  • Server: ${LOG_FILE}"
echo "  • Container: docker logs ${CONTAINER_NAME}"
echo ""

# Ask to follow logs
read -p "Follow server logs? (y/n) " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]; then
    if [[ -f "$LOG_FILE" ]]; then
        log_info "Following logs (Ctrl+C to exit)..."
        sleep 1
        tail -f "$LOG_FILE" | grep --color=auto -E "AuraSkills|ERROR|WARN|Done"
    else
        log_warning "Log file not available yet"
        log_info "Container logs: docker logs ${CONTAINER_NAME}"
    fi
fi

exit 0
