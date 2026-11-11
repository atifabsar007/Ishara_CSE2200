package com.kuet.ishara.logic;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Android-free branching conversation logic; uses immutable nodes, choices and explicit next states. */
public final class ScenarioEngine {
    private ScenarioEngine() { }
    public static final class Choice {
        public final String label;
        public final String response;
        public final String next;
        public final int points;
        public Choice(String label, String response, String next, int points) {
            this.label=label;this.response=response;this.next=next;this.points=points;
        }
    }
    public static final class Node {
        public final String id,prompt,context;
        public final List<Choice> choices;
        public Node(String id,String prompt,String context,Choice... choices) {
            this.id=id;this.prompt=prompt;this.context=context;
            this.choices=Collections.unmodifiableList(Arrays.asList(choices));
        }
    }
    public static final class Scenario {
        public final String id,title,description,start;
        public final Map<String,Node> nodes;
        Scenario(String id,String title,String description,String start, Node... nodeList) {
            this.id=id;this.title=title;this.description=description;this.start=start;
            Map<String,Node> n=new LinkedHashMap<>();for(Node node:nodeList)n.put(node.id,node);
            nodes=Collections.unmodifiableMap(n);
        }
        public Node node(String id){return nodes.get(id);}
    }
    private static Choice c(String label,String response,String next,int points){return new Choice(label,response,next,points);}
    private static Node n(String id,String prompt,String context,Choice... choices){return new Node(id,prompt,context,choices);}
    public static List<Scenario> all() {return Arrays.asList(
        new Scenario("cafe","At a café","Order food with clear, accessible communication.","a1",
            n("a1","The staff asks what you would like.","You can type, gesture-compose, or tap a prepared message.",
                c("আমি পানি চাই।","The staff understands your request.","a2",10),
                c("মেনু দেখাতে পারবেন?","The staff shows the menu so you can choose.","a3",10)),
            n("a2","They ask if you want anything else.","Respond politely and clearly.",
                c("না, ধন্যবাদ।","Your order is complete.","end",10),
                c("আরও একটি পানি চাই।","The extra item is added.","end",10)),
            n("a3","You see the menu. What would help?","You can ask about ingredients or make a choice.",
                c("এই খাবারে কী আছে?","The staff explains the ingredients.","end",10),
                c("আমি এটি চাই।","The staff takes your order.","end",10))),
        new Scenario("clinic","At a clinic","Explain communication needs to a receptionist.","b1",
            n("b1","The receptionist asks how they can help.","Tell them the communication method that works for you.",
                c("দয়া করে লিখে বলুন।","They offer written communication.","b2",10),
                c("আমি একটি অ্যাপ দিয়ে কথা বলি।","They give you time to use Ishara.","b3",10)),
            n("b2","They write an appointment question.","Choose a clear response.",
                c("আমার অ্যাপয়েন্টমেন্ট আছে।","The receptionist checks the schedule.","end",10),
                c("আমি সময় জানতে চাই।","They show available times.","end",10)),
            n("b3","They ask if you need extra time.","You may choose what is comfortable.",
                c("হ্যাঁ, একটু সময় দিন।","They wait while you prepare your response.","end",10),
                c("না, আমি প্রস্তুত।","The conversation continues.","end",10))),
        new Scenario("travel","At a bus stop","Ask for directions and travel information.","c1",
            n("c1","Someone asks what information you need.","Use a short, clear request.",
                c("এই বাস কোথায় যাবে?","They explain the route.","c2",10),
                c("টিকিটের দাম কত?","They show the ticket price.","c3",10)),
            n("c2","They tell you the bus route.","You can ask for confirmation.",
                c("আমাকে এখানে নামাবেন।","The driver acknowledges the stop.","end",10),
                c("ধন্যবাদ।","You acknowledge the help.","end",10)),
            n("c3","They show the ticket price.","You can respond in your own way.",
                c("একটি টিকিট দিন।","They provide the ticket.","end",10),
                c("ধন্যবাদ, আমি পরে আসব।","They understand your decision.","end",10))),
        new Scenario("meeting","Meeting someone","Explain how you communicate and get acquainted.","d1",
            n("d1","A new person greets you.","There are many valid ways to introduce yourself.",
                c("হ্যালো, আমার নাম ...","They greet you back.","d2",10),
                c("আমি লিখে কথা বলতে স্বাচ্ছন্দ্যবোধ করি।","They agree to use text.","d3",10)),
            n("d2","They ask how you'd like to communicate.","Choose your preference.",
                c("আমাকে লিখে জানান।","They type their reply.","end",10),
                c("আমি ইশারা ব্যবহার করি।","They look at your composed message.","end",10)),
            n("d3","They ask what they should do next.","Explain your preferred method.",
                c("ধীরে ধীরে লিখুন।","They type more slowly.","end",10),
                c("আমি আপনাকে বার্তা দেখাব।","You show your screen.","end",10)))
    );}
    public static Scenario byId(String id){for(Scenario s:all())if(s.id.equals(id))return s;return all().get(0);}
}
