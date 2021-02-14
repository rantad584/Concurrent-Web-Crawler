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

    public static String getContentFromURL(String urlString)
    {
        URL url;
        InputStream is = null;
        BufferedReader br;
        String line;

        try
        {
            url = new URL(urlString);
            is = url.openConnection().getInputStream();
            br = new BufferedReader(new InputStreamReader(is));

            StringBuilder sb = new StringBuilder();

            while((line = br.readLine()) != null)
            {
                sb.append(line);
                sb.append("\n");
            }

            return sb.toString();

        }
        catch(MalformedURLException e)
        {
            System.out.println(e.toString());
            e.printStackTrace();
        }
        catch(IOException e)
        {
            //System.out.println(e.toString());
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
            catch(IOException e)
            {
                e.printStackTrace();
                System.out.println(e.toString());
            }
        }
        return "";
    }

    public static ArrayList<String> getHyperlinksFromContent(String urlString, String content)
    {
        ArrayList<String> ret = new ArrayList<>();

        try
        {
            URL url = new URL(urlString);
            Matcher matcher = Pattern.compile("(?i)<a([^>]+)>(.+?)</a>").matcher(content);

            while(matcher.find())
            {
                String links = matcher.group(1);
                Matcher hrefs = Pattern.compile("\\s*(?i)href\\s*=\\s*(\"([^\"]*\")|'[^']*'|([^'\">\\s]+))").matcher(links);

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
        catch(Exception e)
        {
            //System.out.println(e.toString());
        }

        return ret;
    }
}
