# 🚀 Maven Project Setup and Import Guide

## Step 1: Install Maven (if not already done)

### Option A: Manual Installation
1. Download Maven from https://maven.apache.org/download.cgi
2. Extract to `C:\Program Files\Apache\maven`
3. Add to PATH: `C:\Program Files\Apache\maven\bin`
4. Set `MAVEN_HOME`: `C:\Program Files\Apache\maven`
5. Restart your terminal and verify: `mvn --version`

### Option B: Using Chocolatey (Windows Package Manager)
```powershell
# Install Chocolatey first (if not installed)
Set-ExecutionPolicy Bypass -Scope Process -Force; [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072; iex ((New-Object System.Net.WebClient).DownloadString('https://community.chocolatey.org/install.ps1'))

# Install Maven
choco install maven
```

## Step 2: Import Project into IntelliJ IDEA

### Method 1: Open as Maven Project
1. **File** → **Open**
2. Select your project folder (containing `pom.xml`)
3. Click **"Open as Project"**
4. IntelliJ will detect Maven automatically
5. Click **"Import Maven Project"** when prompted
6. Wait for dependency download (first time takes a few minutes)

### Method 2: Import from Existing Sources
1. **File** → **New** → **Project from Existing Sources**
2. Select your project folder
3. Choose **"Import project from external model"**
4. Select **"Maven"**
5. Click **Next** through the wizard
6. Make sure Java SDK 11+ is selected

## Step 3: Configure Project Settings

### Set Project SDK
1. **File** → **Project Structure** → **Project**
2. Set **Project SDK** to Java 11 or higher
3. Set **Project language level** to 11 or higher

### Verify Maven Integration
1. **View** → **Tool Windows** → **Maven**
2. You should see your project with dependencies
3. Click **Reload Maven Projects** (refresh icon)

## Step 4: Fix Common Issues

### Issue 1: JavaFX Module Path
If you get JavaFX module errors, add VM options:
```
--module-path "path/to/javafx/lib" --add-modules javafx.controls,javafx.fxml
```

### Issue 2: Resource Path Issues
Make sure FXML files are in `src/main/resources/view/`

### Issue 3: Database Configuration
1. Edit `src/main/resources/config.properties`
2. Update your database credentials
3. Make sure MySQL is running

## Step 5: Run the Application

### Option 1: Using Maven (Recommended)
```bash
mvn clean compile
mvn javafx:run
```

### Option 2: Using IntelliJ Run Configuration
1. Right-click on `Shop.java` (main class)
2. **Run 'Shop.main()'**
3. If JavaFX errors occur, add VM options in Run Configuration

### Option 3: Build JAR and Run
```bash
mvn clean package
java -jar target/cafe-management-system-1.0.0.jar
```

## Step 6: Verify Everything Works

### Check List:
- [ ] Project compiles without errors
- [ ] JavaFX application starts
- [ ] Database connection works (if configured)
- [ ] FXML files load correctly
- [ ] Images display properly

## Common Troubleshooting

### Maven Not Recognized
- Restart your terminal/IDE after PATH changes
- Check `mvn --version` works in new terminal
- Verify MAVEN_HOME is set correctly

### JavaFX Runtime Issues
- Use `mvn javafx:run` instead of regular Java run
- Add JavaFX module path to VM options
- Ensure JavaFX dependencies are in pom.xml

### Resource Not Found Errors
- Check FXML paths start with `/` (e.g., `/view/user-main-app.fxml`)
- Verify files are in `src/main/resources/`
- Rebuild project: **Build** → **Rebuild Project**

### Database Connection Errors
- Verify MySQL is running
- Check credentials in `config.properties`
- Make sure database exists

## Success! 🎉
Your cafe management system is now properly mavenized and ready to run! 