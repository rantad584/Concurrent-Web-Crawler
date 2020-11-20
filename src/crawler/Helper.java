package crawler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Helper
{
    /* Regular expressions  */
    public static final Pattern link_pattern = Pattern.compile("(?i)<a([^>]+)>(.+?)</a>");
    public static final Pattern href_pattern = Pattern.compile("\\s*(?i)href\\s*=\\s*(\"([^\"]*\")|'[^']*'|([^'\">\\s]+))");

    public static int countNumberOfOccurrences(String word, String text)
    {
        Matcher m = Pattern.compile("\\b" + word + "\\b", Pattern.CASE_INSENSITIVE).matcher(text);
        int matches = 0;
        while(m.find())
        {
            matches++;
        }
        return matches;
    }

    public static String getContentFromURL(String URLstring)
    {
        URL url;
        InputStream is = null;
        BufferedReader br;
        String line;

        try
        {
            url = new URL(URLstring);
            is = url.openStream();
            br = new BufferedReader(new InputStreamReader(is));

            StringBuilder sb = new StringBuilder();

            while((line = br.readLine()) != null)
            {
                sb.append(line);
                sb.append("\n");
            }

            return sb.toString();

        }
        catch(MalformedURLException mue)
        {
            System.out.println(mue.toString());
            mue.printStackTrace();
        }
        catch(IOException ioe)
        {
            //System.out.println(ioe.toString());
        }
        finally
        {
            try
            {
                if(is != null)
                {
                    is.close();
                }
            }
            catch(IOException ioe)
            {
                ioe.printStackTrace();
                System.out.println(ioe.toString());
            }
        }
        return "";
    }

    public static ArrayList<String> getHyperlinksFromContent(String URLstring, String content)
    {

        ArrayList<String> ret = new ArrayList<>();

        try
        {
            URL url = new URL(URLstring);
            Matcher matcher = link_pattern.matcher(content);

            while(matcher.find())
            {
                String links = matcher.group(1);
                Matcher hrefs = href_pattern.matcher(links);

                while(hrefs.find())
                {
                    String link = hrefs.group(1).replaceAll("'", "").replaceAll("\"", "");
                    String absolutePath;
                    String path = url.getPath();
                    int lastSlashPos = path.lastIndexOf('/');
                    if(lastSlashPos >= 0)
                    {
                        absolutePath = path.substring(0, lastSlashPos); //strip off the slash
                    }
                    else
                    {
                        absolutePath = "";
                    }

                    String address;
                    URI uri = new URI(link);
                    if(!uri.isAbsolute())
                    {
                        address = url.getProtocol() + "://" + url.getHost() + absolutePath + "/" + link;

                        if(!address.contains("#"))
                        {
                            ret.add(address);
                        }

                    }
                    else
                    {
                        if(link.contains(url.getHost()))
                        {
                            if(!link.contains("mailto:"))
                            {
                                address = link;
                                if(!address.contains("#"))
                                {
                                    ret.add(address);
                                }
                            }
                        }
                    }
                }
            }
        }
        catch(Exception ex)
        {
            //System.out.println(ex.toString());
        }

        return ret;
    }
}
