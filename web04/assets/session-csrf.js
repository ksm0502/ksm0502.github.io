// The deployed Vue bundle installs this interceptor on its session-based API client.
export function installSessionCsrf(client) {
    const safeMethods = new Set(["get", "head", "options", "trace"]);

    client.interceptors.request.use(async (request) => {
        const method = (request.method ?? "get").toLowerCase();
        if (safeMethods.has(method)) {
            return request;
        }

        // Login and logout replace the session token. Fetch the current token for each write.
        const { data } = await client.get("/csrf");
        request.headers.set(data.headerName, data.token);
        return request;
    });

    return client;
}
