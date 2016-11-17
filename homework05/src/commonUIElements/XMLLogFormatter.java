package commonUIElements;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.LogRecord;
import java.util.logging.XMLFormatter;

/**
 * Class to override existing XML logging functionality to display in a manner
 * that makes more sense for the server.
 * 
 * @author Kyle
 */
public class XMLLogFormatter extends XMLFormatter {

	// date formatter
	private SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd: hh:mm:ss");

	/**
	 * Handles logging a message in XML format.
	 */
	@Override
	public String format(LogRecord logRecord) {
		if(logRecord.getParameters() != null) {
			for(Object obj : logRecord.getParameters()) {
				if(obj instanceof ClientMessage) {
					ClientMessage msg = (ClientMessage)obj;
					StringBuilder sb = new StringBuilder();
					sb.append("<record>\n");
					sb.append("\t<level>" + logRecord.getLevel() + "</level>\n");
					sb.append("\t<date>" + dateFormatter.format(new Date(logRecord.getMillis())) + "</date>\n");
					sb.append("\t\t<serverMsg>");
					sb.append("\t\t\t<client>" + msg.getClientDN() + "</client>");
					sb.append("\t\t\t<message>" + msg.getMessage() + "</message>");
					sb.append("\t\t\t<Clearance>" + msg.getClearance() + "</Clearance>");
					sb.append("\t\t</serverMsg>");
					sb.append("</record>\n");
					return sb.toString();
				}
			}
		}
		return "<record>" + logRecord.getMessage() + "</record>";
	}
}
