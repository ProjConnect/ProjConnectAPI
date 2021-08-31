from fabric import task


@task
def deploy(con, branch="main"):
    code_dir = "~/ProjConnectAPI"
    with con.cd(code_dir):
        print(f"Fetching branch {branch}...")
        con.run(f"git fetch origin {branch}")

        print(f"Switching to branch {branch}...")
        con.run(f"git switch {branch}")

        print("Hard reset branch...")
        con.run("git reset --hard @{{u}}")

        print("Executing deploy script...")
        con.run("sh deploy.sh", pty=False)
