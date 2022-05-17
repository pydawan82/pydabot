package com.pydawan.pydabot.workers;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Vector;

import com.pydawan.pydabot.Announcement;

import org.pircbotx.PircBotX;

import lombok.Getter;
import lombok.Setter;

import static com.pydawan.pydabot.irc.IrcUtils.channelOf;

/**
 * A worker that sends random announcements to a channel.
 * Each call to run() will send a random announcement to a channel.
 */
public class AnnouncementWorker implements Worker {

    private final Vector<Announcement> announcements = new Vector<>();

    /**
     * The bot to which this worker will send messages.
     */
    @Getter
    @Setter
    private volatile PircBotX bot;

    /**
     * Constructs a new AnnouncementWorker.
     * 
     * @param bot The bot to which this worker will send messages.
     */
    public AnnouncementWorker(PircBotX bot) {
        this.bot = bot;
    }

    /**
     * Constructs a new AnnouncementWorker.
     * Adds the given announcement to the list of announcements.
     * 
     * @param bot           The bot to which this worker will send messages.
     * @param announcements The announcements to be sent.
     */
    public AnnouncementWorker(PircBotX bot, Collection<Announcement> announcements) {
        this.bot = bot;
        this.announcements.addAll(announcements);
    }

    /**
     * Sends a random announcement to a channel.
     */
    @Override
    public void run() {
        if (bot == null)
            return;

        Announcement announcement = randomAnnouncement();
        bot.send().message(channelOf(announcement.getChannel()), announcement.getMessage());
    }

    /**
     * Yiels a random announcement.
     * 
     * @return A random announcement.
     */
    private Announcement randomAnnouncement() {
        synchronized (announcements) {
            if (announcements.isEmpty())
                throw new NoSuchElementException("No announcements available");

            double totalWeight = announcements.stream().mapToDouble(Announcement::getWeight).sum();
            double random = Math.random() * totalWeight;
            double currentWeight = 0;

            for (Announcement announcement : announcements) {
                currentWeight += announcement.getWeight();
                if (random < currentWeight)
                    return announcement;
            }

            return announcements.lastElement();
        }
    }

    /**
     * Adds the given announcement to the list of announcements.
     * 
     * @param announcement The announcement to add.
     */
    public void addAnnouncement(Announcement announcement) {
        announcements.add(announcement);
    }

    /**
     * Remove the given announcement to the list of announcements.
     * 
     * @param announcements The announcements to add.
     */
    public void removeAnnouncement(Announcement announcement) {
        announcements.remove(announcement);
    }

    /**
     * Clears the list of announcements.
     */
    public void clearAnnouncements() {
        announcements.clear();
    }

    /**
     * Returns an unmodifiable view of the list of announcements.
     * 
     * @return The announcements.
     */
    public List<Announcement> getAnnouncements() {
        return Collections.unmodifiableList(announcements);
    }

}
