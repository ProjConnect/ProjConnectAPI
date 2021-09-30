from fabric import task


@task
def deploy(con, branch="main", stage="prod"):
    code_dir = f"~/{stage}/ProjConnectAPI"
    with con.cd(code_dir):
        print(f"Fetching branch {branch}...")
        con.run(f"git fetch origin {branch}")

        print(f"Switching to branch {branch}...")
        con.run(f"git switch {branch} -f")

        print("Hard reset branch...")
        con.run(f"git reset --hard @{{u}}")

        print("Compiling App")
        con.run("./gradlew installDist")

        print("Building Image")
        con.run(f"docker build -t ktor-api:{stage} .")

        print("Running Docker Compose")
        con.run(f"docker-compose -f docker-compose-{stage}.yml up -d")
