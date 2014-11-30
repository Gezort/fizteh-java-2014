package ru.fizteh.fivt.students.LevkovMiron.Proxy;

import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.IdentityHashMap;

/**
 * Created by Мирон on 24.11.2014 ru.fizteh.fivt.students.LevkovMiron.Proxy.
 */
public class CLoggingProxyFactory implements LoggingProxyFactory {

    private XMLParser xmlParser = new XMLParser();

    @Override
    public Object wrap(Writer writer, Object implementation, Class<?> interfaceClass) {
        Object result = Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class[]{interfaceClass},
                new MyHandler(writer, implementation));
        return result;
    }

    class MyHandler implements InvocationHandler {

        private Writer writer;
        private Object implementation;

        MyHandler(Writer wr, Object impl) {
            writer = wr;
            implementation = impl;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            writer.write("<invoke timestamp=\"" + System.currentTimeMillis()
                    + "\" class=\"" + implementation + "\" name=\"" + method.getName() + "\">");
            if (args != null && args.length > 0) {
                writer.write(xmlParser.parse(args));
            } else {
                writer.write("<arguments/>");
            }
            try {
                Object result;
                if (args != null) {
                    result = method.invoke(implementation, args);
                } else {
                    result = method.invoke(implementation);
                }
                if (method.getReturnType() != void.class) {
                    String res;
                    if (result == null || result instanceof Iterable) {
                        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                        Document document = builder.newDocument();
                        Transformer transformer = TransformerFactory.newInstance().newTransformer();
                        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
                        StringWriter stringWriter = new StringWriter();
                        transformer.transform(new DOMSource(xmlParser.parseObject(result, new IdentityHashMap<>(), document)),
                                new StreamResult(stringWriter));
                        res = stringWriter.toString();
                    } else {
                        res = result.toString();
                    }
                    writer.write("<return>" + res + "</return>");
                }
                return result;
            } catch (InvocationTargetException e) {
                writer.write("<thrown>" + e.getTargetException() + "</thrown>");
                writer.flush();
                throw e;
            } finally {
                writer.write("</invoke>");
            }
        }
    }
}
