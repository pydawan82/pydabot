package com.pydawan.pydabot.workers;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Vector;

import com.pydawan.pydabot.Announce;

import org.pircbotx.PircBotX;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import static com.pydawan.pydabot.irc.IrcUtils.channelOf;

public class AnnounceWorker implements Worker {

    private final Vector<Announce> announces = new Vector<>();

    @Getter
    @Setter
    private volatile PircBotX bot;

    public AnnounceWorker(PircBotX bot) {
        this.bot = bot;
    }

    @Override
    public void run() {
        if (bot == null)
            return;

        Announce announce = randomAnnounce();
        bot.send().message(channelOf(announce.getChannel()), announce.getMessage());
    }

    private Announce randomAnnounce() {
        synchronized (announces) {
            if (announces.isEmpty())
                throw new NoSuchElementException("No announces available");

            double totalWeight = announces.stream().mapToDouble(Announce::getWeight).sum();
            double random = Math.random() * totalWeight;
            double currentWeight = 0;
            for (Announce announce : announces) {
                currentWeight += announce.getWeight();
                if (random < currentWeight)
                    return announce;
            }

            return announces.lastElement();
        }
    }

    public void addAnnounce(Announce announce) {
        announces.add(announce);
    }

    public void removeAnnounce(Announce announce) {
        announces.remove(announce);
    }

    public void clearAnnounces() {
        announces.clear();
    }

    public List<Announce> getAnnounces() {
        return Collections.unmodifiableList(announces);
    }

}
