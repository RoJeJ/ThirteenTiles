import com.smartfoxserver.v2.SmartFoxServer;
import com.smartfoxserver.v2.extensions.ISFSExtension;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ResServlet extends HttpServlet {
    private ISFSExtension ext;
    @Override
    public void init() throws ServletException {
        ext = SmartFoxServer.getInstance().getZoneManager().getZoneByName("thirteenTiles").getExtension();
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String userID = req.getParameter("UserID");
        String message = (String) ext.handleInternalMessage("updateDia",userID);
        resp.getWriter().print(message);
    }
}
